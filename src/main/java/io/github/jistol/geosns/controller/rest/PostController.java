package io.github.jistol.geosns.controller.rest;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Result.badRequest;
import static io.github.jistol.geosns.util.Result.success;

@Slf4j
@ControllerAdvice
@RestController
@RequestMapping("/rest/map/post")
public class PostController {
    @Autowired private PostService postService;
    @Autowired private StorageService storageService;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> savePost(HttpSession httpSession,
                                    @RequestParam(name = "files", required = false) MultipartFile[] files,
                                    Post post) throws IOException, InvocationTargetException, IllegalAccessException {
        Post saved = postService.save(httpSession, post, files);
        return success(entry("post", saved));
    }

    @PutMapping("")
    public ResponseEntity<Map<String, Object>> updateePost(HttpSession httpSession,
                                                        @RequestParam(name = "files", required = false) MultipartFile[] files,
                                                        Post updatePost) throws IOException, InvocationTargetException, IllegalAccessException {
        Post saved = postService.update(httpSession, updatePost, files);
        return success(entry("post", saved));
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> loadPost(HttpSession session, LatLngBound bound) {
        User user = SessionUtil.loadUser(session);
        List<Map<String, Object>> posts = postService.load(user, bound);
        return success(entry("post", posts));
    }

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewPost(HttpServletRequest req, PostForm postForm) {
        User user = SessionUtil.loadUser(req.getSession());
        Post post = postService.view(req, user, postForm);
        if (post == null) {
            return badRequest(Code.postViewFail);
        } else {
            return success(entry("post", post));
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
