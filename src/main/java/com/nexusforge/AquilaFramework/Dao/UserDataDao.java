package com.nexusforge.AquilaFramework.dao;

import com.nexusforge.AquilaFramework.entity.User;
import com.nexusforge.AquilaFramework.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDataDao {

    @Autowired
    UserRepository userRepository;

    public User getUserDataByUserEmailDao(String email) {
        User userInfo = new User();
        Optional<User> userData = userRepository.findByEmail(email);

        if(userData.isPresent()){
            userInfo = userData.get();
        }

        return userInfo;
    }
}
