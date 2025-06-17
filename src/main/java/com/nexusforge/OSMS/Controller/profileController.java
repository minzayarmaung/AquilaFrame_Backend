package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.User;
import com.nexusforge.OSMS.Mgr.userDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class profileController {

    @Autowired
    userDataMgr userDataMgr;

    @RequestMapping("/getUserData")
    public User getUserData(@RequestBody Map<String , String> body){
        User userInfo = new User();
        userInfo = userDataMgr.getUserDataByEmail(body);
        return userInfo;
    }
}
