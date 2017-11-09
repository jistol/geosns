package io.github.jistol.geosns.service;

import com.google.common.collect.Maps;
import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.dao.PostDao;
import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.model.PostForm;
import io.github.jistol.geosns.type.Code;
import io.github.jistol.geosns.util.Crypt;
import io.github.jistol.geosns.util.SessionUtil;
import io.github.jistol.geosns.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.jistol.geosns.util.Cast.string;

@Service
public class PostService {
    @Autowired @Qualifier("postLimit") private Pageable postLimit;
    @Autowired private StorageService storageService;
    @Autowired private PostDao postDao;
    @Autowired @Qualifier("baseCrypt") private Crypt baseCrypt;

    public Post save(HttpSession httpSession, Post post, MultipartFile[] files) throws IOException {
        post.setUser(SessionUtil.loadUser(httpSession));
        post.setAttaches(storageService.store(files, "post", post.getId()));
        post.setCreatedDate(new Date());
        post.setUpdatedDate(new Date());
        return postDao.save(post);
    }

    public Post update(HttpSession httpSession, Post updatePost, MultipartFile[] files) throws IOException {
        Post post = postDao.findPost(updatePost.getId(), SessionUtil.loadUser(httpSession));
        post.setMessage(updatePost.getMessage());
        post.setScope(updatePost.getScope());
        post.setUpdatedDate(new Date());

        Collection<Attach> attachList = storageService.store(files, "post", post.getId());
        Collection<Long> attachIds = updatePost.getAttachIds();
        for (Attach attach : post.getAttaches()) {
            if (!Util.contains(attachIds, attach.getId())) {
                storageService.remove(attach);
            } else {
                attachList.add(attach);
            }
        }
        post.setAttaches(attachList);
        return postDao.save(post);
    }

    public List<Map<String, Object>> load(User user, LatLngBound b) {
        if (b.getWest() > b.getEast()) {
            return postDao.findBoundsDateLine(b.getWest(), b.getNorth(), b.getEast(), b.getSouth(), user, postLimit);
        } else {
            return postDao.findBounds(b.getWest(), b.getNorth(), b.getEast(), b.getSouth(), user, postLimit);
        }
    }

    public Post view(HttpServletRequest req, User user, PostForm postForm) {
        Post post = postDao.findPost(postForm.getId(), user);
        if (post != null) {
            post.setAttachInfo(Optional.ofNullable(post.getAttaches())
                                        .orElse(new ArrayList<>()).stream()
                                        .map(attach -> {
                                            Map<String, Object> map = Maps.newHashMap();
                                            map.put("id", attach.getId());
                                            map.put("url", string("/download/post/", storageService.getSignedKey(req, attach, GeoSnsRuntimeException.func())));
                                            map.put("size", attach.getSize());
                                            return map;
                                        })
                                        .collect(Collectors.toList()));

            post.setOwner(user != null && post.getUser().getId() == user.getId());
            post.setEncId(baseCrypt.encrypt(Long.toString(post.getUser().getId()), GeoSnsRuntimeException.func()));
        }

        return post;
    }
}
