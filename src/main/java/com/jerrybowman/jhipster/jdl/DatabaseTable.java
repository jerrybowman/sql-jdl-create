package com.jerrybowman.jhipster.jdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseTable {
    private final String name;
    private final List<TableColumn> columnList = new ArrayList<>();

    public DatabaseTable(String name) {
        this.name = name;
    }

    public DatabaseTable addColumn(TableColumn column) {
        columnList.add(column);
        return this;
    }

    public String getName() {
        return name;
    }

    public List<TableColumn> getColumnList() {
        return columnList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseTable that = (DatabaseTable) o;
        return Objects.equals(name, that.name) && Objects.equals(columnList, that.columnList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columnList);
    }

    @Override
    public String toString() {
        return "DatabaseTable{" +
                "name='" + name + '\'' +
                ", columnList=" + columnList +
                '}';
    }
}
