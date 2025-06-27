package com.nexusforge.AquilaFramework.dto;

import com.nexusforge.AquilaFramework.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private long syskey;
    private String username;
    private String email;
    private String phone;
    private long status;
    private String createddate;
    private long n1; // this is likely your role
    private long n2;
    private long n3;
    private long n4;
    private long n5;

    public UserDTO() {}

}
