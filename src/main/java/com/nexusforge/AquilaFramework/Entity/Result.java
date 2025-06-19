package com.nexusforge.AquilaFramework.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String msgCode;
    private String msgDesc;
    private boolean state;

}
