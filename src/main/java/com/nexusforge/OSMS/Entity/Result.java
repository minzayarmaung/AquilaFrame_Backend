package com.nexusforge.OSMS.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private boolean state;
    private String msgCode;
    private String msgDesc;

}
