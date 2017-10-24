package io.github.jistol.geosns.util;

import io.github.jistol.geosns.jpa.entry.User;

import javax.servlet.http.HttpSession;

public class SessionUtil {
    private static final String userKey = "__GEOSNS_USERINFO__";

    public static User storeUser(HttpSession session, User user) {
        session.setAttribute(userKey, user);
        return user;
    }

    public static User loadUser(HttpSession session) {
        return (User)session.getAttribute(userKey);
    }

    public static void removeUser(HttpSession session) {
        session.removeAttribute(userKey);
    }
}
