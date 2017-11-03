package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.dao.PostDao;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    @Autowired @Qualifier("postLimit") private Pageable postLimit;
    @Autowired private StorageService storageService;
    @Autowired private PostDao postDao;


    public Post save(HttpSession httpSession, Post post, MultipartFile[] files) throws IOException {
        post.setUser(SessionUtil.loadUser(httpSession));
        post.setAttaches(storageService.store(files, "post", post.getId()));
        post.setCreatedDate(new Date());
        return postDao.save(post);
    }

    public List<Post> load(LatLngBound b) {
        if (b.getWest() > b.getEast()) {
            return postDao.findBoundsDateLine(b.getWest(), b.getNorth(), b.getEast(), b.getSouth(), postLimit);
        } else {
            return postDao.findBounds(b.getWest(), b.getNorth(), b.getEast(), b.getSouth(), postLimit);
        }
    }
}
