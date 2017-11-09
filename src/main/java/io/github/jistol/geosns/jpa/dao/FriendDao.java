package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.Friend;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("friendDao")
public interface FriendDao extends JpaRepository<Friend, Long> {
    Friend findFriend(@Param("friendId") Long friendId, @Param("user") User user);
}
