package org.stjude.alsac.template.jhipster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

@ShellComponent
public class SqlJdlCommands {
    private static Logger LOG = LoggerFactory.getLogger(SqlJdlCommands.class);

    private static final String jdbcUrlTemplate = "jdbc:sqlserver://%s;database=%s;integratedSecurity=true;TrustServerCertificate=True";

    private String connectionUrl;
    private Connection dbConnection;

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

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dbConnection = DriverManager.getConnection(connectionUrl);
    }

    @ShellMethod("List table data.")
    public void listTableData() throws SQLException {
        try (Statement statement = dbConnection.createStatement()) {
            ResultSet rs = statement.executeQuery(listTableData);
            Formatter fmt = new Formatter();
            String format = "%-64s %-30s %-16s %15s\n";
            fmt.format(format, "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "CHARACTER_MAXIMUM_LENGTH");
            String lastTableName = "";
            List<DatabaseTable> tableList = new ArrayList<>();
            DatabaseTable databaseTable = null;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                String characterMaximumLength = rs.getString("CHARACTER_MAXIMUM_LENGTH");
                if (!lastTableName.equals(tableName)) {
                    if (databaseTable != null) {
                        fmt.format(format,
                                databaseTable.getName(), "", "", ""
                        );
                        databaseTable.getColumnList().forEach(column -> {
                            fmt.format(format,
                                    "", column.getName(), column.getDataType(), column.getMaxSize()
                            );
                        });
                    }
                    databaseTable = new DatabaseTable(tableName);
                    tableList.add(databaseTable);
                    lastTableName = tableName;
                }
                databaseTable.addColumn(new TableColumn(columnName, dataType, characterMaximumLength));
            }
            System.out.println(fmt);
            System.out.println("Number of tables: " + tableList.size());
        }
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