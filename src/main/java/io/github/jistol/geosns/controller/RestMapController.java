package io.github.jistol.geosns.controller;

import com.google.common.collect.Maps;
import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.model.PostForm;
import io.github.jistol.geosns.service.PostService;
import io.github.jistol.geosns.service.StorageService;
import io.github.jistol.geosns.type.Code;
import io.github.jistol.geosns.type.Scope;
import io.github.jistol.geosns.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;
import static io.github.jistol.geosns.util.Cast.string;

@Slf4j
@ControllerAdvice
@RestController
@RequestMapping("/rest/map")
public class RestMapController {
    @Autowired private PostService postService;
    @Autowired private StorageService storageService;

    @PostMapping(value = "/post")
    public ResponseEntity<Map<String, Object>> savePost(HttpSession httpSession,
                                    @RequestParam(name = "files", required = false) MultipartFile[] files,
                                    Post post) throws IOException, InvocationTargetException, IllegalAccessException {
        Post saved = postService.save(httpSession, post, files);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("msg", "success"), entry("post", saved)));
    }

    @PutMapping(value = "/post")
    public ResponseEntity<Map<String, Object>> updateePost(HttpSession httpSession,
                                                        @RequestParam(name = "files", required = false) MultipartFile[] files,
                                                        Post updatePost) throws IOException, InvocationTargetException, IllegalAccessException {
        Post saved = postService.update(httpSession, updatePost, files);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("msg", "success"), entry("post", saved)));
    }

    @GetMapping(value = "/post")
    public ResponseEntity<Map<String, Object>> loadPost(HttpSession session, LatLngBound bound) {
        User user = SessionUtil.loadUser(session);
        List<Map<String, Object>> posts = postService.load(user, bound);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("posts", posts)));
    }

    @GetMapping(value = "/post/view")
    public ResponseEntity<Map<String, Object>> viewPost(HttpServletRequest req, PostForm postForm) {
        User user = SessionUtil.loadUser(req.getSession());
        Post post = postService.view(user, postForm);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map(entry("code", HttpStatus.BAD_REQUEST)));
        } else {
            Function<Exception, GeoSnsRuntimeException> exFun = e -> new GeoSnsRuntimeException(Code.etcError, e.getMessage());
            post.setAttachInfo(Optional.ofNullable(post.getAttaches())
                                        .orElse(new ArrayList<>()).stream()
                                        .map(attach -> {
                                            Map<String, Object> map = Maps.newHashMap();
                                            map.put("id", attach.getId());
                                            map.put("url", string("/download/post/", storageService.getSignedKey(req, attach, exFun)));
                                            map.put("size", attach.getSize());
                                            return map;
                                        })
                                        .collect(Collectors.toList()));
            return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("post", post)));
        }
    }


    @InitBinder
    public void enumScopeBinding(WebDataBinder binder)
    {
        binder.registerCustomEditor(Scope.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) throws IllegalArgumentException
            {
                super.setValue(Scope.valueOf(text));
            }
        });
    }
}
