package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Mgr.userAuthMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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

    @PostMapping("/verifySignupCode")
    public Result verifySignupCode(@RequestBody Map<String , String> body){
        Result res = new Result();
        res = userAuthMgr.verifySignUpCode(body);
        if(res.isState()){
            res = userAuthMgr.saveNewUser(body);
        }
        return res;
    }

    @PostMapping("/resendSignupCode")
    public Result resendSignupCode(@RequestBody Map<String , String> body){
        Result res = new Result();
        String email = body.get("email");
        res = userAuthMgr.sendSignupVerifyEmail(email);
        return res;
    }
}
