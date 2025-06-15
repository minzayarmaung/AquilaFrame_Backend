package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Mgr.userAuthMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/signupController")
public class signupController {

    @Autowired
    private userAuthMgr userAuthMgr;

    @PostMapping("/signup")
    public Result signUpController(@RequestBody Map<String , String> body){
        Result res = new Result();
        res = userAuthMgr.signUpUserMgr(body);
        return res;
    }
}
