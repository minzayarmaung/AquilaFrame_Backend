package com.nexusforge.AquilaFramework.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnDto {
    private String name;
    private String type;
    private boolean isPrimaryKey;
    private boolean isNotNull;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }
}
