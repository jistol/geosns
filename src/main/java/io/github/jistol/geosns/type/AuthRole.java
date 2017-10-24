package io.github.jistol.geosns.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum AuthRole {
    ADMIN,
    USER;

    public static String[] any() {
        List<String> authList = new ArrayList<>();
        Stream.of(AuthRole.values()).forEach(authRole -> authList.add(authRole.name()));
        return authList.toArray(new String[authList.size()]);
    }
}
