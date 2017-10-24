package io.github.jistol.geosns.kakao.controller;

import io.github.jistol.geosns.kakao.http.KakaoApiHttp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Response;

import java.io.IOException;

import static io.github.jistol.geosns.util.Cast.string;

@Slf4j
@Controller
@RequestMapping("/admin/kakao")
public class KakaoAdminController {
    @Value("${kakao.admin-key}") private String adminKey;
    @Autowired private KakaoApiHttp kakaoApiHttp;

    @org.springframework.web.bind.annotation.ResponseBody
    @RequestMapping("/unlink")
    public String unlinkByAdmin(@RequestParam("userId") String userId) throws IOException {
        Response<ResponseBody> result = kakaoApiHttp.callUnlinkByAdmin(string("KakaoAK ", adminKey), userId).execute();
        if (!result.isSuccessful()) {
            // exception
            String msg = result.errorBody().string();
            log.error("error unlink by admin : {}", msg);
            return string("ERROR : ", msg);
        } else {
            log.debug("call unlink by admin : {}", result.body().string());
            return "OK";
        }
    }
}
