package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public interface UserDao extends JpaRepository<User, Long> {
    User findBySiteIdAndLoginType(String siteId, LoginType loginType);
}
