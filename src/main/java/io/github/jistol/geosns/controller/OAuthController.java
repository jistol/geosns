package io.github.jistol.geosns.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Enumerated;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Controller
public class OAuthController {
    @ResponseBody
    @RequestMapping("/login/kakao/complete")
    public String complete(HttpServletRequest request, Principal principal) {
        /*Map<String, String[]> paramMap = request.getParameterMap();
        paramMap.forEach((key, values) -> {
            String value = Stream.of(values).reduce((v1, v2) -> v1 + "," + v2).get();
            log.debug("key[{}]:{}", key, value);
        });*/
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> map = (HashMap<String, Object>) authentication.getUserAuthentication().getDetails();

        map.forEach((key, value) -> {
            log.debug("key[{}]:{}", key, value);
        });

        /*Enumeration e = request.getParameterNames();
        while(e.hasMoreElements()) {
            Object key = e.nextElement();
            log.debug("key[{}]:{}", key, request.getParameter(key.toString()));
        }*/
        log.debug("Principal : {}", principal);
        return principal.toString();
    }
}
