package com.jerrybowman.jhipster.jdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ShellComponent
public class SqlJdlCommands {
    private static Logger LOG = LoggerFactory.getLogger(SqlJdlCommands.class);

    private static final String jdbcUrlTemplate = "jdbc:sqlserver://%s;database=%s;integratedSecurity=true;TrustServerCertificate=True";

    private String connectionUrl;
    private Connection dbConnection;

    @PostConstruct
    public void init() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @PreDestroy
    public void shutdown() {
        if (dbConnection != null) {
            LOG.info("Closing database connection for: {}", connectionUrl);
            try {
                dbConnection.close();
            } catch (SQLException e) {
                LOG.warn("Error on db connection close", e);
            }
            dbConnection = null;
        }
    }

    @ShellMethod("Connect to a SQL server.")
    public void connectToServer(String serverName, String databaseName) throws ClassNotFoundException, SQLException {
        if (dbConnection != null) {
            shutdown();
        }
        connectionUrl = String.format(jdbcUrlTemplate, serverName, databaseName);
        LOG.info("Driver string: {}", connectionUrl);
        dbConnection = DriverManager.getConnection(connectionUrl);
    }

    @ShellMethod("List table data.")
    public void listTableData(@ShellOption(defaultValue = "false") boolean asJdl) throws SQLException {
        try (Statement statement = dbConnection.createStatement()) {
            ResultSet rs = statement.executeQuery(listTableData);
            String lastTableName = "";
            List<DatabaseTable> tableList = new ArrayList<>();
            DatabaseTable databaseTable = null;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                String characterMaximumLength = rs.getString("CHARACTER_MAXIMUM_LENGTH");
                String constraintType = rs.getString("CONSTRAINT_TYPE");
                if (!lastTableName.equals(tableName)) {
                    databaseTable = new DatabaseTable(tableName);
                    lastTableName = tableName;
                    tableList.add(databaseTable);
                }
                databaseTable.addColumn(new TableColumn(columnName, dataType, characterMaximumLength, constraintType));
            }
            if (asJdl) {
                printJdl(tableList);
            } else {
                printTablesAndColumns(tableList);
            }

        }
    }


    private void printJdl(List<DatabaseTable> tableList) {
        tableList.forEach(table -> {
            System.out.println("entity " + normalizeName(table.getName()) + " {");
            for (int i = 0; i < table.getColumnList().size(); i++) {
                TableColumn column = table.getColumnList().get(i);
                if (primaryKey(column)) continue;
                System.out.print("    " + normalizeName(column.getName()) +
                        " " + convertDataType(column.getDataType()));
                if (column.getMaxSize() != null && !column.getMaxSize().equals("-1")) {
                    System.out.print(" maxlength(" + column.getMaxSize() + ")");
                }
                if (i == table.getColumnList().size() - 1) {
                    System.out.println("\n}");
                } else {
                    System.out.println(",");
                }
            }
            ;
        });
    }

    private boolean primaryKey(TableColumn column) {
        if (column.getConstraintType() == null) return false;
        return (column.getConstraintType().equals("PRIMARY KEY")) &&
                (column.getDataType().equals("uniqueidentifier") ||
                        column.getDataType().equals("decimal") ||
                        column.getDataType().equals("numeric") ||
                        column.getDataType().equals("bigint")
                );
    }

    private String convertDataType(String dataType) {
        switch (dataType) {
            case "datetime":
            case "datetime2":
                return "ZonedDateTime";
            case "decimal":
                return "BigDecimal";
            case "bit":
                return "Boolean";
            case "tinyint":
            case "int":
                return "Integer";
            case "uniqueidentifier":
            case "bigint":
                return "Long";
            case "nvarchar":
            case "varchar":
                return "String";
        }
        return "String";
    }

    private void printTablesAndColumns(List<DatabaseTable> tableList) {
        AtomicInteger tableNameLength = new AtomicInteger();
        AtomicInteger columnNameLength = new AtomicInteger();
        AtomicInteger dataTypeLength = new AtomicInteger();
        AtomicInteger maxSizeLength = new AtomicInteger(4);
        tableList.forEach(table -> {
            if (table.getName().length() > tableNameLength.get())
                tableNameLength.set(table.getName().length());
            table.getColumnList().forEach(column -> {
                if (column.getName().length() > columnNameLength.get())
                    columnNameLength.set(table.getName().length());
                if (column.getDataType().length() > dataTypeLength.get())
                    dataTypeLength.set(column.getDataType().length());
                if ((column.getMaxSize() != null) &&
                        (column.getMaxSize().length() > maxSizeLength.get())) {
                    maxSizeLength.set(column.getMaxSize().length());
                }
            });
        });
        Formatter fmt = new Formatter();
        String format = "%-" + tableNameLength.get() +
                "s %-" + columnNameLength.get() +
                "s %-" + dataTypeLength.get() +
                "s %" + maxSizeLength.get() + "s\n";
        fmt.format(format, "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "CHARACTER_MAXIMUM_LENGTH");
        tableList.forEach(table -> {
            fmt.format(format,
                    table.getName(), "", "", ""
            );
            table.getColumnList().forEach(column -> {
                fmt.format(format,
                        "", column.getName(), column.getDataType(), column.getMaxSize()
                );
            });
        });
        System.out.println(fmt);
        System.out.println("Number of tables: " + tableList.size());
    }

    private String normalizeName(String name) {
        StringBuilder sb = new StringBuilder();
        boolean nextLetterUpperCase = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0) {
                sb.append(Character.toLowerCase(c));
                continue;
            }
            if (c == '_') {
                nextLetterUpperCase = true;
                continue;
            }
            if (nextLetterUpperCase) {
                sb.append(Character.toUpperCase(c));
                nextLetterUpperCase = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }


    String listTableData = "SELECT 'sqlserver' dbms, " +
            "  t.TABLE_CATALOG, t.TABLE_SCHEMA, t.TABLE_NAME, c.COLUMN_NAME, c.ORDINAL_POSITION, c.DATA_TYPE, " +
            "  c.CHARACTER_MAXIMUM_LENGTH, n.CONSTRAINT_TYPE, k2.TABLE_SCHEMA, k2.TABLE_NAME, k2.COLUMN_NAME" +
            "    FROM INFORMATION_SCHEMA.TABLES t" +
            "    LEFT JOIN INFORMATION_SCHEMA.COLUMNS c" +
            "    ON t.TABLE_CATALOG=c.TABLE_CATALOG" +
            "    AND t.TABLE_SCHEMA=c.TABLE_SCHEMA" +
            "    AND t.TABLE_NAME=c.TABLE_NAME" +
            "    LEFT JOIN(INFORMATION_SCHEMA.KEY_COLUMN_USAGE k" +
            "               JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS n" +
            "                       ON k.CONSTRAINT_CATALOG=n.CONSTRAINT_CATALOG" +
            "                       AND k.CONSTRAINT_SCHEMA=n.CONSTRAINT_SCHEMA" +
            "                       AND k.CONSTRAINT_NAME=n.CONSTRAINT_NAME" +
            "                       LEFT JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS r" +
            "                       ON k.CONSTRAINT_CATALOG=r.CONSTRAINT_CATALOG" +
            "                       AND k.CONSTRAINT_SCHEMA=r.CONSTRAINT_SCHEMA" +
            "                       AND k.CONSTRAINT_NAME=r.CONSTRAINT_NAME)" +
            "    ON c.TABLE_CATALOG=k.TABLE_CATALOG" +
            "    AND c.TABLE_SCHEMA=k.TABLE_SCHEMA" +
            "    AND c.TABLE_NAME=k.TABLE_NAME" +
            "    AND c.COLUMN_NAME=k.COLUMN_NAME" +
            "    LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE k2" +
            "    ON k.ORDINAL_POSITION=k2.ORDINAL_POSITION" +
            "    AND r.UNIQUE_CONSTRAINT_CATALOG=k2.CONSTRAINT_CATALOG" +
            "    AND r.UNIQUE_CONSTRAINT_SCHEMA=k2.CONSTRAINT_SCHEMA" +
            "    AND r.UNIQUE_CONSTRAINT_NAME=k2.CONSTRAINT_NAME" +
            "    WHERE t.TABLE_TYPE='BASE TABLE'" +
            "    order by t.table_name";
}