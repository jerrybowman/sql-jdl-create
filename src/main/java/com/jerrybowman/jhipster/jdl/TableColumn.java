package com.jerrybowman.jhipster.jdl;

import java.util.Objects;

public class TableColumn {
    private final String name;
    private final String dataType;
    private final String maxSize;
    private final String constraintType;
    private ColumnConstraint constraint;

    public TableColumn(String name, String dataType, String maxSize, String constraintType, ColumnConstraint constraint) {
        this(name, dataType, maxSize, constraintType);
        this.constraint = constraint;
    }

    public TableColumn(String name, String dataType, String maxSize, String constraintType) {
        this.name = name;
        this.dataType = dataType;
        this.maxSize = maxSize;
        this.constraintType = constraintType;
        this.constraint = null;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public ColumnConstraint getConstraint() {
        return constraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumn that = (TableColumn) o;
        return Objects.equals(name, that.name) && Objects.equals(dataType, that.dataType) && Objects.equals(maxSize, that.maxSize) && Objects.equals(constraintType, that.constraintType) && Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType, maxSize, constraintType, constraint);
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", maxSize='" + maxSize + '\'' +
                ", constraintType='" + constraintType + '\'' +
                ", constraint=" + constraint +
                '}';
    }
}
