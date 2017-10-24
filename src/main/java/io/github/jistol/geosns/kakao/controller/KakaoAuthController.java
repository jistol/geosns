package io.github.jistol.geosns.kakao.controller;

import io.github.jistol.geosns.kakao.model.KakaoToken;
import io.github.jistol.geosns.kakao.service.KakaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Slf4j
//@Controller
//@RequestMapping("/auth/kakao")
public class KakaoAuthController {
    @Autowired private KakaoService kakaoService;

    @RequestMapping("/login")
    public String login(HttpSession session,
                        @RequestParam(name = "service", required = false) String service) {
        String redirectUrl = kakaoService.getLoginRedirectUrl(session, service);
        return redirectUrl;
    }

    @RequestMapping("/login/callback")
    public String loginCallback(HttpSession session,
                                     @RequestParam("code") String code,
                                     @RequestParam("state") String state) throws Exception {
        log.debug("login callback result - code : {}, state : {}", code, state);
        kakaoService.validateState(session, state);

        KakaoToken kakaoToken = kakaoService.getToken(code);
        log.debug("info token result : {}", kakaoToken);

        kakaoService.login(session, kakaoToken);
        return kakaoService.getRedirectService(state);
    }
}
