package com.nexusforge.AquilaFramework.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String msgCode;
    private String msgDesc;
    private boolean state;
    private String token;

    public void setData(Map<String, String> token) {
    }
}
