package io.github.jistol.geosns.controller;

import io.github.jistol.geosns.jpa.dao.UserDao;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.type.LoginType;
import io.github.jistol.geosns.util.SessionUtil;
import io.github.jistol.geosns.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.string;

@Slf4j
@Controller
public class OAuthController {
    @Autowired private UserDao userDao;

    @RequestMapping("/login/{social}/complete")
    public RedirectView complete(HttpSession session, @PathVariable("social") String social) {
        OAuth2Authentication authentication = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
        log.debug("/login/complete -> isAuthenticated : {}, grant : {}", authentication.isAuthenticated(), authentication.getAuthorities());
        Map<String, Object> map = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
        log.debug("auth : {}", authentication.getAuthorities());
        map.forEach((key, value) -> {
            log.debug("key[{}]:{}", key, value);
        });

        SessionUtil.storeUser(session, SocialMapper.valueOf(social).loadUser(userDao, map));
        return new RedirectView("/map");
    }

    private enum SocialMapper {
        google {
            @Override public User loadUser(UserDao userDao, Map<String, Object> map) {
                final String siteId = string(map.get("id"));
                User user = userDao.findBySiteIdAndLoginType(siteId, LoginType.google);
                if (user == null) {
                    Boolean isEmailVerified = (Boolean)map.get("verified_email");
                    user = new User();
                    user.setSiteId(siteId);
                    user.setLoginType(LoginType.google);
                    user.setNickname((String)map.get("name"));
                    if (isEmailVerified != null && isEmailVerified) {
                        user.setEmail((String)map.get("email"));
                    }
                    user.setProfileImage((String)map.get("picture"));
                    user.setThumbnailImage((String)map.get("picture"));

                    userDao.save(user);
                }

                return user;
            }
        },
        facebook {
            @Override public User loadUser(UserDao userDao, Map<String, Object> map) {
                final String siteId = string(map.get("id"));
                User user = userDao.findBySiteIdAndLoginType(siteId, LoginType.facebook);
                if (user == null) {
                    Boolean isEmailVerified = (Boolean)map.get("verified_email");
                    user = new User();
                    user.setSiteId(siteId);
                    user.setLoginType(LoginType.facebook);
                    user.setNickname((String)map.get("name"));
                    if (isEmailVerified != null && isEmailVerified) {
                        user.setEmail((String)map.get("email"));
                    }

                    String url = (String)Util.getFromMap(map, "picture", "data", "url");
                    user.setProfileImage(url);
                    user.setThumbnailImage(url);

                    userDao.save(user);
                }

                return user;
            }
        },
        kakao {
            @Override public User loadUser(UserDao userDao, Map<String, Object> map) {
                final String siteId = string(map.get("id"));
                User user = userDao.findBySiteIdAndLoginType(siteId, LoginType.kakao);
                if (user == null) {
                    Map<String, Object> prop = (Map<String, Object>)map.get("properties");
                    Boolean isEmailVerified = (Boolean)map.get("kaccount_email_verified");

                    user = new User();
                    user.setSiteId(siteId);
                    user.setLoginType(LoginType.kakao);
                    user.setNickname((String)prop.get("nickname"));
                    if (isEmailVerified != null && isEmailVerified) {
                        user.setEmail((String)map.get("kaccount_email"));
                    }
                    user.setProfileImage((String)prop.get("profile_image"));
                    user.setThumbnailImage((String)prop.get("thumbnail_image"));

                    userDao.save(user);
                }

                return user;
            }
        };

        abstract public User loadUser(UserDao userDao, Map<String, Object> map);
    }
}
