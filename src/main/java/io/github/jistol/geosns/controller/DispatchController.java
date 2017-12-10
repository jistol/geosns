package io.github.jistol.geosns.controller;

import io.github.jistol.geosns.type.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@Controller
public class DispatchController {
    @RequestMapping({"/map", "/map/**"})
    public ModelAndView map() {
        AbstractAuthenticationToken auth = (AbstractAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        log.debug("/map -> isAuthenticated : {}, grant : {}", auth.isAuthenticated(), auth.getAuthorities());
        if (auth instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication)auth;
            Map<String, Object> map = (Map<String, Object>) authentication.getUserAuthentication().getDetails();

            log.debug("auth : {}", authentication.getAuthorities());

            map.forEach((key, value) -> {
                log.debug("key[{}]:{}", key, value);
            });
        }

        return new ModelAndView("view/index")
                .addObject("loginUser", Session.loadUser());
    }

    @RequestMapping("/setup")
    public ModelAndView setup() {
        if (Session.loadUser() == null) {
            return new ModelAndView("view/redirect")
                    .addObject("callUrl", "/map");
        }

        return map();
    }
}
