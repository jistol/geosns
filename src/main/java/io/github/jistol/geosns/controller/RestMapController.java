package io.github.jistol.geosns.controller;

import com.google.common.net.HttpHeaders;
import io.github.jistol.geosns.jpa.entry.Post;
import io.github.jistol.geosns.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@Slf4j
@RestController
@RequestMapping("/rest/map")
public class RestMapController {
    @Autowired private PostService postService;

    @PostMapping(value = "/post")
    public Map<String, Object> post(HttpSession httpSession,
                                    @RequestParam(value = "attaches", required = false) MultipartFile[] attaches,
                                    @RequestParam("message") String message) throws IOException {
        Post post = postService.save(httpSession, message, attaches);
        log.debug("do posting : {}", post);
        return map(entry("code", "0000"), entry("msg", "success"));
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = null;//storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping("/test")
    public Map<String, Object> test() {
        return map(entry("a", "A"), entry("b", "B"));
    }
}
