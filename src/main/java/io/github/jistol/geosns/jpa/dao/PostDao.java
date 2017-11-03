package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("postDao")
public interface PostDao extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.lng >= :west AND p.lng <= :east AND p.lat <= :north AND p.lat >= :south ORDER BY p.createdDate DESC")
    List<Post> findBounds(@Param("west") Double west, @Param("north") Double north, @Param("east") Double east, @Param("south") Double south, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.lng <= :west AND p.lng >= :east AND p.lat <= :north AND p.lat >= :south ORDER BY p.createdDate DESC")
    List<Post> findBoundsDateLine(@Param("west") Double west, @Param("north") Double north, @Param("east") Double east, @Param("south") Double south, Pageable pageable);
}
