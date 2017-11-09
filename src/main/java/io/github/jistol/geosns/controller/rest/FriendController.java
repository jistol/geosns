package io.github.jistol.geosns.controller.rest;

import io.github.jistol.geosns.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@Slf4j
@RestController
@RequestMapping("/rest/map/friend")
public class FriendController {
    @Autowired private FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> savePost(HttpSession httpSession, @RequestParam("encId") String encId) throws IOException, InvocationTargetException, IllegalAccessException {
        boolean result = friendService.request(httpSession, encId);
        return ResponseEntity.ok(map(entry("code", HttpStatus.OK.value()), entry("msg", "success"), entry("result", result)));
    }

}
