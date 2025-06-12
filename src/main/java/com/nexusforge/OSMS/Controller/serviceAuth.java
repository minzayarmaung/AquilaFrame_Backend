package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Mgr.userAuthMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/serviceAuth")
public class serviceAuth {

    @Autowired
    private userAuthMgr userAuthMgr;

    @PostMapping("/forgotPassword")
    public Result sendResetEmail(@RequestBody Map<String , String> body){
        Result res = new Result();
        String email = body.get("email");
        res = userAuthMgr.sendResetEmail(email);
        return res;
    }
}
