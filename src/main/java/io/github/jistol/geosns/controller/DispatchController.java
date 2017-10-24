package io.github.jistol.geosns.controller;

import io.github.jistol.geosns.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class DispatchController {
    @RequestMapping({"/map", "/map/index", "/map/main"})
    public ModelAndView map(HttpSession session) {
        return new ModelAndView("view/map")
                .addObject("loginUser", SessionUtil.loadUser(session));
    }
}
