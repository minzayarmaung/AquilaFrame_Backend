package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.PasswordResetToken;
import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Mgr.userAuthMgr;
import com.nexusforge.OSMS.Repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authController")
public class authController {

    @Autowired
    private userAuthMgr userAuthMgr;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @PostMapping("/forgotPassword")
    private Result sendResetEmail(@RequestBody Map<String , String> body){
        Result res = new Result();
        String email = body.get("email");
        res = userAuthMgr.checkEmailExistInSystem(email);
        if(res.isState()){
            res = userAuthMgr.sendResetEmail(email);
        }
        return res;
    }

    @PostMapping("/verifyResetCode")
    private Result verifyResetCode(@RequestBody Map<String , String> body){
        Result res = new Result();
        String email = body.get("email");
        String code = body.get("code");
        res = userAuthMgr.verifyResetCode(email , code);
        return res;
    }

    @PostMapping("/resetPassword")
    private Result resetPassword(@RequestBody Map<String , String> body){
        Result res = new Result();
        String email = body.get("email");
        String newPassword = body.get("password");
        res = userAuthMgr.resetPassword(email , newPassword);
        return res;
    }
}
