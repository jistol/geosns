package io.github.jistol.geosns.kakao.type;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kakao.auth.urls")
public class KakaoAuthUrlProp {
    private String callbackReqCode;
    private String reqCode;
    private String restReqToken;
    private String signup;
}
