package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("postingDao")
public interface PostDao extends JpaRepository<Post, Long> {
}
