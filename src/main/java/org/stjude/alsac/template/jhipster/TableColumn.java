package org.stjude.alsac.template.jhipster;

import java.util.Objects;

public class TableColumn {
    private final String name;
    private final String dataType;
    private final String maxSize;

    public TableColumn(String name, String dataType, String maxSize) {
        this.name = name;
        this.dataType = dataType;
        this.maxSize = maxSize;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumn that = (TableColumn) o;
        return Objects.equals(name, that.name) && Objects.equals(dataType, that.dataType) && Objects.equals(maxSize, that.maxSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType, maxSize);
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", maxSize=" + maxSize +
                '}';
    }
}
