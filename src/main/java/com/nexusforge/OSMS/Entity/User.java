package com.nexusforge.OSMS.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Table(name = "uvm001")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long syskey;
    private long autokey;
    private String createddate;
    private String modifieddate;
    private long status;
    private String username;
    private String password;
    private String email;
    private String phone;
    private long parentid;
    private long n1;
    private long n2;
    private long n3;
    private long n4;
    private long n5;


}
