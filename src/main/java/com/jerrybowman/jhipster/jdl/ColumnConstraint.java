package com.jerrybowman.jhipster.jdl;

import java.util.Objects;

public class ColumnConstraint {
    private final String constraintTableName;
    private final String constraintColumnName;

    public ColumnConstraint(String constraintTableName, String constraintColumnName) {
        this.constraintTableName = constraintTableName;
        this.constraintColumnName = constraintColumnName;
    }

    public String getConstraintTableName() {
        return constraintTableName;
    }

    public String getConstraintColumnName() {
        return constraintColumnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnConstraint that = (ColumnConstraint) o;
        return Objects.equals(constraintTableName, that.constraintTableName) && Objects.equals(constraintColumnName, that.constraintColumnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraintTableName, constraintColumnName);
    }

    @Override
    public String toString() {
        return "ColumnConstraint{" +
                "constraintTableName='" + constraintTableName + '\'' +
                ", constraintColumnName='" + constraintColumnName + '\'' +
                '}';
    }
}
