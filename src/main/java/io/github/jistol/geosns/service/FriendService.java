package io.github.jistol.geosns.service;

import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.dao.FriendDao;
import io.github.jistol.geosns.jpa.entry.Friend;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.Session;
import io.github.jistol.geosns.util.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class FriendService {
    @Autowired @Qualifier("baseCrypt") private Crypt baseCrypt;
    @Autowired private FriendDao friendDao;

    public boolean request(HttpSession session, String encId) {
        User user = Session.loadUser();
        Long friendId = Long.parseLong(baseCrypt.decrypt(encId, GeoSnsRuntimeException.func()));
        Friend friend = friendDao.findFriend(friendId, user);
        if (friend == null) {
            return false;
        }

        friend.getRequest().add(user);
        friendDao.save(friend);
        return true;
    }
}
