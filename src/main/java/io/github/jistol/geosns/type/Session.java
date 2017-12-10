package io.github.jistol.geosns.type;

import com.google.common.collect.Lists;
import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.entry.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.github.jistol.geosns.util.Cast.string;


public class Session {
    private static final String prefix = "__GEOSNS_SESSION__";

    private final String parentKey;
    private final HttpSession session;

    private Session(String... args) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        session = attributes.getRequest().getSession();
        parentKey = string(prefix, session.getId(), Arrays.stream(Optional.ofNullable(args).orElse(new String[0])).reduce((s1, s2) -> s1 + s2).orElse(""));
    }

    private String getKey(Object key) {
        return string(parentKey, key);
    }

    public <T> T load(Object key, Class<T> type){
        return type.cast(session.getAttribute(getKey(key)));
    }

    public <T> T store(Object key, T data) {
        session.setAttribute(getKey(key), data);
        return data;
    }

    public void remove(String key) {
        session.removeAttribute(getKey(key));
    }

    public void clear() {
        Enumeration<String> names = session.getAttributeNames();
        List<String> removeTarget = Lists.newArrayList();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith(parentKey)) {
                removeTarget.add(name);
            }
        }

        removeTarget.stream().forEach(name -> session.removeAttribute(name));
    }

    public static Session get(String... args) {
        return new Session(args);
    }

    public static User loadUser() {
        return Session.get().load("user", User.class);
    }

    public static User storeUser(User user) {
        return Session.get().store("user", user);
    }

    public static void validateLogin(Function<Code, ? extends RuntimeException> func) {
        User user = loadUser();
        if (user == null) {
            throw func.apply(Code.requiredLogin);
        }
    }
}
