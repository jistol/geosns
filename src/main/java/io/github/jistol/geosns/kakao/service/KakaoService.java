package io.github.jistol.geosns.kakao.service;

import io.github.jistol.geosns.exception.GeoSnsRuntimeException;
import io.github.jistol.geosns.jpa.entry.User;
import io.github.jistol.geosns.kakao.http.KakaoApiHttp;
import io.github.jistol.geosns.kakao.http.KakaoOAuthHttp;
import io.github.jistol.geosns.kakao.model.KakaoToken;
import io.github.jistol.geosns.kakao.type.KakaoAuthUrlProp;
import io.github.jistol.geosns.service.UserService;
import io.github.jistol.geosns.type.BaseProps;
import io.github.jistol.geosns.type.Code;
import io.github.jistol.geosns.type.LoginType;
import io.github.jistol.geosns.util.SessionUtil;
import io.github.jistol.geosns.util.Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import retrofit2.Response;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.msgFormat;
import static io.github.jistol.geosns.util.Cast.string;

@Slf4j
@Service
public class KakaoService {
    private final String loginStateKey = "/auth/kakao/login#state"; // 검증키

    @Value("${kakao.auth.oauth-url}") private String oauthUrl;
    @Value("${kakao.rest-app-key}") private String appKey;
    @Value("${kakao.client-secret}") private String clientSecret;

    @Autowired private UserService userService;

    @Autowired private BaseProps baseProps;
    @Autowired private KakaoAuthUrlProp kakaoAuthUrlProp;

    @Autowired private KakaoApiHttp kakaoApiHttp;
    @Autowired private KakaoOAuthHttp kakaoOAuthHttp;

    public String getLoginRedirectUrl(HttpSession session, String service) {
        String state = string(Util.isEmpty(service)?"map":service, "||", session.getId(), Util.getCurrentTimeStamp());
        session.setAttribute(loginStateKey, state);
        return string("redirect:", oauthUrl, kakaoAuthUrlProp.getReqCode(), "&state=", state);
    }

    public void validateState(HttpSession session, String state) {
        if (!Util.equals(state, (String)session.getAttribute(loginStateKey))) {
            // exception
        }
    }

    public String getRedirectService(String state) {
        String url = baseProps.getServiceUrl().get(state.split("\\|\\|")[0]);
        log.debug("redirect url  : {}", url);
        return "redirect:" + url;
    }

    public KakaoToken getToken(String code) throws IOException, InvocationTargetException, IllegalAccessException {
        Response<ResponseBody> resToken = kakaoOAuthHttp.callToken(appKey, kakaoAuthUrlProp.getCallbackReqCode(), code, clientSecret).execute();
        if (!resToken.isSuccessful()) {
            // exception
        }

        return new KakaoToken(Util.jsonToMap(resToken.body().string()));
    }

    public KakaoToken refreshToken(String token) throws IOException, InvocationTargetException, IllegalAccessException {
        Response<ResponseBody> refreshRes = kakaoOAuthHttp.callTokenRefresh(appKey, token, clientSecret).execute();
        if (!refreshRes.isSuccessful()) {
            //exception
        }

        return new KakaoToken(Util.jsonToMap(refreshRes.body().string()));
    }

    public User<KakaoToken> login(HttpSession session, KakaoToken kakaoToken) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        return login(session, kakaoToken, false);
    }

    private User<KakaoToken> login(HttpSession session, KakaoToken kakaoToken, boolean isRecursive) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        KakaoToken token = (KakaoToken)BeanUtils.cloneBean(kakaoToken);
        // 사용자 토큰 유효성 검사 및 정보 얻기
        Response<ResponseBody> resTokenInfo = kakaoApiHttp.callAccessTokenInfo(string("Bearer ", token.getAccessToken())).execute();
        if (!resTokenInfo.isSuccessful()) {
            switch (resTokenInfo.code()) {
                case 400:
                    throw new GeoSnsRuntimeException(Code.wrongToken);  // 잘못된 토큰
                case 401:
                    if (isRecursive) {
                        throw new GeoSnsRuntimeException(Code.recursiveError);  // 반복적으로 만료된 토큰이 넘어올 경우
                    }
                    // 만료된 토큰
                    log.debug("[{}]: expired token", resTokenInfo.code());
                    token = refreshToken(token.getRefreshToken());
                    return login(session, token, true);
                default:
                    log.error("[{}]: error access token info", resTokenInfo.code());
                    throw new GeoSnsRuntimeException(Code.kakaoEtcError, string(resTokenInfo.code(), resTokenInfo.message()));
            }
        }

        String result = resTokenInfo.body().string();
        Map<String, Object> tokenInfoMap = Util.jsonToMap(result);
        if (!tokenInfoMap.containsKey("appId") || Double.parseDouble(tokenInfoMap.getOrDefault("appId", "-1").toString()) < 0) {
            // 앱 연결 필요
            tokenInfoMap.put("id", signup(token.getAccessToken()));
        }

        User<KakaoToken> user = userService.storeIfAbsent(tokenInfoMap.get("id").toString(), LoginType.kakao);
        user.setData(token);
        return SessionUtil.storeUser(session, user);
    }

    private String signup(String token) throws IOException {
        Response<ResponseBody> resSignup = kakaoApiHttp.callSignup(string("Bearer ", token)).execute();
        if (!resSignup.isSuccessful()) {    // 앱 연결 실패
            throw new GeoSnsRuntimeException(Code.kakaoSingupError);
        }

        Map<String, Object> result = Util.jsonToMap(resSignup.body().string());
        log.debug("call signup result : {}", result);
        return result.get("id").toString();
    }
}
