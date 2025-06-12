package com.nexusforge.OSMS.Controller;

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
    public ResponseEntity<?> sendResetEmail(@RequestBody Map<String , String> body){
        String email = body.get("email");
        userAuthMgr.sendResetEmail(email);
        return ResponseEntity.ok().body("Send Email Successfully to " + email);
    }
}
