package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.jpa.entry.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("postDao")
public interface PostDao extends JpaRepository<Post, Long> {

    Post findPost(@Param("id") Long id, @Param("user") User user);

    List<Map<String, Object>> findBounds(@Param("west") Double west, @Param("north") Double north, @Param("east") Double east, @Param("south") Double south, @Param("user") User user, Pageable pageable);

    List<Map<String, Object>> findBoundsDateLine(@Param("west") Double west, @Param("north") Double north, @Param("east") Double east, @Param("south") Double south, @Param("user") User user, Pageable pageable);
}
