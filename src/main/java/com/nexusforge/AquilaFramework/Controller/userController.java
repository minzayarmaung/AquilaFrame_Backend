package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.Entity.User;
import com.nexusforge.AquilaFramework.Mgr.userDataMgr;
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
public class userController {

    @Autowired
    userDataMgr userDataMgr;

    @PostMapping("/getUserData")
    public User getUserData(@RequestBody Map<String , String> body){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInfo = new User();
        userInfo = userDataMgr.getUserDataByUserNameMgr(username);
        return userInfo;
    }
}
