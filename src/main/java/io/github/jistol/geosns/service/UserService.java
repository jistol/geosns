package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.dao.UserDao;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.LoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserDao userDao;

    public User store(String siteId, LoginType loginType) {
        User user = new User();
        user.setSiteId(siteId);
        user.setLoginType(loginType);

        return userDao.save(user);
    }

    public User storeIfAbsent(String siteId, LoginType loginType) {
        User user = userDao.findBySiteIdAndLoginType(siteId, loginType);
        return (user == null)? store(siteId, loginType) : user;
    }
}
