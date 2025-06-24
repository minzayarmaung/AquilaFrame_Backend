package com.nexusforge.AquilaFramework.Mgr;

import com.nexusforge.AquilaFramework.Dao.userDataDao;
import com.nexusforge.AquilaFramework.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class userDataMgr {

    @Autowired
    userDataDao userDataDao;

    public User getUserDataByUserNameMgr(String username) {
        return userDataDao.getUserDataByUserNameDao(username);
    }

}
