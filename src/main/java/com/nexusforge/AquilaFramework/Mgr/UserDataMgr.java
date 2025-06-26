package com.nexusforge.AquilaFramework.Mgr;

import com.nexusforge.AquilaFramework.dao.UserDataDao;
import com.nexusforge.AquilaFramework.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDataMgr {

    @Autowired
    UserDataDao userDataDao;

    public User getUserDataByUserEmailMgr(String email) {
        return userDataDao.getUserDataByUserEmailDao(email);
    }

    public List<com.nexusforge.AquilaFramework.entity.User> getAllUserDataMgr() {
        return userDataDao.getAllUserDataDao();
    }
}
