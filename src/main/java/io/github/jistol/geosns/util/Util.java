package io.github.jistol.geosns.util;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import org.springframework.util.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Util {
    public static Map<String, Object> jsonToMap(String json) {
        return new Gson().fromJson(json, Map.class);
    }

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static String mapToJson(Map map) {
        return new Gson().toJson(map);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return false;
        }

        return str1 == null? str2.equals(str1) : str1.equals(str2);
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return ((String) value).isEmpty();
        } else if (value instanceof Map) {
            return ((Map) value).isEmpty();
        } else if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        } else {
            return false;
        }
    }

    public static <T> boolean contains(Collection<T> col, T dest) {
        if (col == null) {
            return false;
        }

        return col.contains(dest);
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        return Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR")
                .sequential()
                .map(name -> request.getHeader(name))
                .filter(value -> !Util.isEmpty(value) && !"unknown".equalsIgnoreCase(value))
                .findFirst()
                .orElseGet(() -> request.getRemoteAddr());
    }

    public static <T extends RuntimeException> T newRuntimeExceptionInstance(Class<T> clazz, Object obj) {
        try {
            return clazz.getDeclaredConstructor(obj.getClass()).newInstance(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String snakeToCamel(String snake) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, snake);
    }

    public static <T extends Number> T getNumber(String num, Class<T> clazz) {
        Double d = Double.parseDouble(num);
        return NumberUtils.convertNumberToTargetClass(d, clazz);
    }

    public static <T> T getIfEmpty(T value, T defaultValue) {
        return isEmpty(value)? defaultValue : value;
    }

    public static <T> void doIfPresent(T param, Consumer<T> consumer) {
        if (!isEmpty(param)) {
            consumer.accept(param);
        }
    }

    public static long calFromNow(long dest) {
        return (System.currentTimeMillis() - dest) / 1000;
    }

    public static Object getFromMap(Map map, Object... pathList)
    {
        Map tempMap = null;
        Object result = map;
        for (Object path : pathList)
        {
            tempMap = (Map)result;
            result = tempMap.get(path);
        }
        return result;
    }
}
