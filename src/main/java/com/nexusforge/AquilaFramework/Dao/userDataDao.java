package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Entity.User;
import com.nexusforge.AquilaFramework.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class userDataDao {

    @Autowired
    UserRepository userRepository;

    public User getUserDataByEmail(String email) {
        User userInfo = new User();
        Optional<User> userData = userRepository.findByEmail(email);

        if(userData.isPresent()){
            userInfo = userData.get();
        }

        return userInfo;
    }
}
