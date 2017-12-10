package io.github.jistol.geosns.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.dao.PostDao;
import io.github.jistol.geosns.jpa.entry.Attach;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.model.Meta;
import io.github.jistol.geosns.model.PostForm;
import io.github.jistol.geosns.type.Session;
import io.github.jistol.geosns.util.Crypt;
import io.github.jistol.geosns.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.jistol.geosns.util.Cast.string;

@Service
public class PostService {
    @Autowired @Qualifier("postLimit") private Pageable postLimit;
    @Autowired private StorageService storageService;
    @Autowired private PostDao postDao;
    @Autowired @Qualifier("baseCrypt") private Crypt baseCrypt;

    private List<Attach> store(Long id, Collection<Attach> attaches, Collection<Meta> metas, Collection<Long> attachIds, MultipartFile[] files) {
        Collection<Long> safeIds = Util.getIfEmpty(attachIds, Lists.newArrayList());
        // remove dummy attach
        Map<Long, Attach> attachMap = Optional.ofNullable(attaches)
                                            .orElse(Lists.newArrayList())
                                            .stream()
                                            .filter(attach -> {
                                                boolean b = safeIds.contains(attach.getId());
                                                if (!b) {
                                                    storageService.remove(attach, GeoSnsRuntimeException.func());
                                                }
                                                return b;
                                            })
                                            .reduce(Maps.newHashMap(),
                                                    (map, attach) -> { map.put(attach.getId(), attach); return map; },
                                                    (map1, map2) -> { map1.putAll(map2); return map1; });


        AtomicInteger idx = new AtomicInteger(0);
        return Optional.ofNullable(metas).orElse(Lists.newArrayList()).stream()
                .reduce(Lists.newArrayList(),
                (list, pf) -> {
                    switch (pf.getType()) {
                        case Meta.typeId:
                            list.add(attachMap.get(pf.getKey()).setup(pf)); break;
                        case Meta.typeFile:
                            list.add(storageService.store(files[idx.getAndIncrement()], "post", id, GeoSnsRuntimeException.func()).setup(pf)); break;
                    }
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
    }

    public Post save(Post post, MultipartFile[] files) throws IOException {
        post.setUser(Session.loadUser());
        post.setAttaches(store(post.getId(), post.getAttaches(), post.getMetas(), post.getAttachIds(), files));
        post.setCreatedDate(new Date());
        post.setUpdatedDate(new Date());
        return postDao.save(post);
    }

    public Post update(Post updatePost, MultipartFile[] files) throws IOException {
        Post post = postDao.findPost(updatePost.getId(), Session.loadUser());
        post.setMessage(updatePost.getMessage());
        post.setScope(updatePost.getScope());
        post.setUpdatedDate(new Date());
        post.setAttaches(store(post.getId(), post.getAttaches(), updatePost.getMetas(), updatePost.getAttachIds(), files));
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
                                            map.put("deltaX", attach.getDeltaX());
                                            map.put("deltaY", attach.getDeltaY());
                                            return map;
                                        })
                                        .collect(Collectors.toList()));

            post.setOwner(user != null && post.getUser().getId() == user.getId());
            post.setEncId(baseCrypt.encrypt(Long.toString(post.getUser().getId()), GeoSnsRuntimeException.func()));
        }

        return post;
    }
}
