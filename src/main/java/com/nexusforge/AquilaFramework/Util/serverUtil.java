package com.nexusforge.AquilaFramework.Util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class serverUtil {

    public String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String generateRandomCode(){
        return UUID.randomUUID().toString().substring(0,8);
    }

    public static String ipAddress = "";

    public String getLocalDateTime(){
        LocalDateTime date = LocalDateTime.now(ZoneId.of("Asia/Rangoon"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return date.format(formatter);
    }
    public String getLocalDate(){
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Rangoon"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }

    public boolean checkIllegalArgument(String value){
        if(value.matches("^[a-zA-Z][a-zA-Z0-9_]*$")){
            return false;
        } else {
            return true;
        }
    }
}
