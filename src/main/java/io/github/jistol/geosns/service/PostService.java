package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.dao.PostDao;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    @Value("${post.load-limit}") private Integer loadLimit;
    @Autowired private StorageService storageService;
    @Autowired private PostDao postDao;

    public Post save(HttpSession httpSession, Post post, MultipartFile[] files) throws IOException {
        post.setUser(SessionUtil.loadUser(httpSession));
        post.setAttaches(storageService.store(files, "post", post.getId()));
        post.setCreatedDate(new Date());
        return postDao.save(post);
    }

    public List<Post> load(LatLngBound b) {
        if (b.getWest() < 0 && b.getEast() > 0) {
            List<Post> list1 = postDao.findBounds(b.getWest(), b.getNorth(), -180.0, b.getSouth());
            List<Post> list2 = postDao.findBounds(b.getEast(), b.getNorth(), 180.0, b.getSouth());

            list1.addAll(list2);
            list1.sort(Comparator.comparing(Post::getCreatedDate).reversed());
            return list1.subList(0, list1.size() > 30 ?  30 : list1.size());
        } else {
            return postDao.findBounds(b.getWest(), b.getNorth(), b.getEast(), b.getSouth());
        }
    }
}
