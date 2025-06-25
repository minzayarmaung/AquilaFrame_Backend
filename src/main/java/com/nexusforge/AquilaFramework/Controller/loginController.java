package com.nexusforge.AquilaFramework.controller;

import com.nexusforge.AquilaFramework.entity.Result;
import com.nexusforge.AquilaFramework.Mgr.UserAuthMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/loginController")
public class LoginController {

    @Autowired
    private UserAuthMgr userAuthMgr;

    @PostMapping("/login")
    private Result loginUser(@RequestBody Map<String , String> body) {
        Result res = new Result();
        String email = body.get("email");
        String password = body.get("password");
        res = userAuthMgr.verifyLoginUser(email , password);
        return res;
    }
}
