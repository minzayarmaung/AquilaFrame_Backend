package com.nexusforge.AquilaFramework.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_verify_token")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String token;

    private LocalDateTime expiry;

    // Constructors
    public PasswordResetToken() {
    }

    public PasswordResetToken(String email, String token, LocalDateTime expiry) {
        this.email = email;
        this.token = token;
        this.expiry = expiry;
    }
}
