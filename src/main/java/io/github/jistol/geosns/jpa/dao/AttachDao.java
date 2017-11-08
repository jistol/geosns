package io.github.jistol.geosns.jpa.dao;

import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.jpa.entry.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("attachDao")
public interface AttachDao extends JpaRepository<Attach, Long> {
}
