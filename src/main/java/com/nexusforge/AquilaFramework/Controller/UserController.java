package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.dto.UserDTO;
import com.nexusforge.AquilaFramework.entity.User;
import com.nexusforge.AquilaFramework.Mgr.UserDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/getAllUsers")
    public List<User> getAllUserData(){
        return userDataMgr.getAllUserDataMgr();
    }

    @GetMapping("/searchUser")
    public List<UserDTO> searchUsers(@RequestParam String searchVal){
        return userDataMgr.getSearchUserDataMgr(searchVal);
    }
}
