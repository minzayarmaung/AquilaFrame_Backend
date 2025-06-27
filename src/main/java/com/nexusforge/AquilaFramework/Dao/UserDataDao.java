package com.nexusforge.AquilaFramework.dao;

import com.nexusforge.AquilaFramework.Util.DtoUtil;
import com.nexusforge.AquilaFramework.Dto.UserDTO;
import com.nexusforge.AquilaFramework.Entity.User;
import com.nexusforge.AquilaFramework.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDataDao {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DtoUtil dtoUtil;

    public User getUserDataByUserEmailDao(String email) {
        User userInfo = new User();
        Optional<User> userData = userRepository.findByEmail(email);

        if(userData.isPresent()){
            userInfo = userData.get();
        }

        return userInfo;
    }

    public List<com.nexusforge.AquilaFramework.Entity.User> getAllUserDataDao() {
        List<User> result = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for(int i=0; i<users.size(); i++){
            result.add(users.get(i));
        }
        return result;
    }

    public List<UserDTO> getSearchUserDataDao(String searchVal) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchVal, searchVal);
        List<UserDTO> result = new ArrayList<>();

        for (User user : users) {
            UserDTO dto = dtoUtil.convertToDTO(user);
            result.add(dto);
        }

        return result;
    }

}
