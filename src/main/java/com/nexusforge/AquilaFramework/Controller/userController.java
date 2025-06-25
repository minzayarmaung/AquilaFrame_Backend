package com.nexusforge.AquilaFramework.controller;

import com.nexusforge.AquilaFramework.entity.User;
import com.nexusforge.AquilaFramework.Mgr.UserDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/userController")
public class UserController {

    @Autowired
    UserDataMgr userDataMgr;

    @PostMapping("/getUserData")
    public User getUserData(@RequestBody Map<String , String> body){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = body.get("email");
//        String username = authentication.getName();
        User userInfo = new User();
        userInfo = userDataMgr.getUserDataByUserEmailMgr(email);
        return userInfo;
    }
}
