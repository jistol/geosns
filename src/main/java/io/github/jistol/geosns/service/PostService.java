package io.github.jistol.geosns.service;

import io.github.jistol.geosns.jpa.dao.PostDao;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@Service
public class PostService {
    @Autowired private StorageService storageService;
    @Autowired private PostDao postDao;

    public Post save(HttpSession httpSession, Post post, MultipartFile[] files) throws IOException {
        post.setUser(SessionUtil.loadUser(httpSession));
        post.setAttaches(storageService.store(files, "post", post.getId()));
        return postDao.save(post);
    }
}
