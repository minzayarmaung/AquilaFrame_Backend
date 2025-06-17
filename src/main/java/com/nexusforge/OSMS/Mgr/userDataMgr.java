package com.nexusforge.OSMS.Mgr;

import com.nexusforge.OSMS.Dao.userDataDao;
import com.nexusforge.OSMS.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class userDataMgr {

    @Autowired
    userDataDao userDataDao;

    public User getUserDataByEmail(Map<String, String> body) {
        User userInfo = new User();
        String email = body.get("email");
        userInfo = userDataDao.getUserDataByEmail(email);
        return userInfo;
    }
}
