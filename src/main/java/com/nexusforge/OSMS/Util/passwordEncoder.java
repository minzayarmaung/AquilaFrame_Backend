package com.nexusforge.OSMS.Util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class passwordEncoder {

    public String encodePassword(String plainPassword) {
        return Base64.getEncoder().encodeToString(plainPassword.getBytes());
    }

    public String decodePassword(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        String encodedInput = encodePassword(rawPassword);
        return encodedInput.equals(encodedPassword);
    }
}
