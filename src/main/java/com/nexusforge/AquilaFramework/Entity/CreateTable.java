package com.nexusforge.AquilaFramework.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTable {

    private String tableName;
    private List<ColumnDefinition> columns;
    @JsonProperty("isUpdate")
    private boolean isUpdate;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }

    @Getter
    @Setter
    public static class ColumnDefinition {
        private String name;
        private String type;

        @JsonProperty("isPrimaryKey")
        private boolean isPrimaryKey;

        @JsonProperty("isNotNull")
        private boolean isNotNull;

    }
}
