package com.nexusforge.AquilaFramework.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HelloResource {

    @GetMapping("/helloResource")
    public static String testing(){
        return "Hello Resource";
    }
}
