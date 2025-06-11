package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.LoginRequest;
import com.nexusforge.OSMS.Entity.Result;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/serviceLogin")
public class serviceLogin {

    String userName = "zayar@gmail.com";
    String password = "123";

    @PostMapping("/login")
    private Result loginUser(@RequestBody LoginRequest loginRequest) {
        Result res = new Result();
        res.setState(false);

        if (loginRequest.getEmail().equalsIgnoreCase(userName) &&
                loginRequest.getPassword().equalsIgnoreCase(password)) {

            res.setState(true);
            res.setMsgDesc("Login Successful!");
            res.setMsgCode("200");

        } else if (loginRequest.getEmail().equalsIgnoreCase(userName) &&
                !loginRequest.getPassword().equalsIgnoreCase(password)) {

            res.setState(false);
            res.setMsgDesc("Invalid Password!");
            res.setMsgCode("500");
        } else {
            res.setState(false);
            res.setMsgDesc("Invalid Credentials!");
            res.setMsgCode("500");
        }
        return res;
    }
}
