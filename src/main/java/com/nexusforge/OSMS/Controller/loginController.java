package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.LoginRequest;
import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Mgr.userAuthMgr;
import com.nexusforge.OSMS.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/loginController")
public class loginController {

    @Autowired
    private userAuthMgr userAuthMgr;

    @PostMapping("/login")
    private Result loginUser(@RequestBody Map<String , String> body) {
        Result res = new Result();
        String email = body.get("email");
        String password = body.get("password");
        res = userAuthMgr.verifyLoginUser(email , password);
        return res;
    }
}
