package io.github.jistol.geosns.kakao.model;

import io.github.jistol.geosns.util.Util;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class KakaoToken implements Serializable {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private long expiresIn;
    private String scope;

    public KakaoToken(Map<String, Object> map) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> cvtMap = new HashMap<>();
        map.forEach((key, value) -> cvtMap.put(Util.snakeToCamel(key), map.get(key)));
        BeanUtils.populate(this, cvtMap);
    }
}
