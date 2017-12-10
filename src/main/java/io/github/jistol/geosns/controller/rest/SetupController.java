package io.github.jistol.geosns.controller.rest;

import io.github.jistol.geosns.exception.GeoSnsRestRuntimeException;
import io.github.jistol.geosns.jpa.dao.UserDao;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.model.PostForm;
import io.github.jistol.geosns.type.Code;
import io.github.jistol.geosns.type.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.*;

@Slf4j
@ControllerAdvice
@RestController
@RequestMapping("/rest/setup")
public class SetupController {
    @Autowired private UserDao userDao;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> viewPost(HttpServletRequest req, PostForm postForm) {
        Session.validateLogin(GeoSnsRestRuntimeException.funcByCode());
        return ResponseEntity.ok(map(entry("user", Session.loadUser())));
    }

    @PutMapping("/user/nickname")
    public ResponseEntity<Map<String, Object>> updateUserName(@RequestParam("nickname") String nickname) {
        Session.validateLogin(GeoSnsRestRuntimeException.funcByCode());
        User user = Session.loadUser();
        user.setNickname(nickname);
        userDao.save(user);
        return ResponseEntity.ok(Code.success.toMap());
    }


}
