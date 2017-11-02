package io.github.jistol.geosns.controller;

import com.google.common.net.HttpHeaders;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.model.LatLngBound;
import io.github.jistol.geosns.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@Slf4j
@RestController
@RequestMapping("/rest/map")
public class RestMapController {
    @Autowired private PostService postService;

    @PostMapping(value = "/post")
    public ResponseEntity<Map<String, Object>> savePosting(HttpSession httpSession,
                                    @RequestParam(name = "files", required = false) MultipartFile[] files,
                                    Post post) throws IOException, InvocationTargetException, IllegalAccessException {
        Post saved = postService.save(httpSession, post, files);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("msg", "success"), entry("post", saved)));
    }

    @GetMapping(value = "/post")
    public ResponseEntity<Map<String, Object>> loadPosting(LatLngBound bound) {
        List<Post> posts = postService.load(bound);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("posts", posts)));
    }


    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = null;//storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
