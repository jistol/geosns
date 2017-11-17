package io.github.jistol.geosns.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static io.github.jistol.geosns.util.Cast.string;

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

    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return false;
        }

        return obj1 == null? obj2.equals(obj1) : obj1.equals(obj2);
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

    public static String camelToSnake(String camel) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel);
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


    public static <K, V> Map<K, V> joinMap(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> tmp = new HashMap<>();
        tmp.putAll(map1);
        tmp.putAll(map2);
        return tmp;
    }

    public static <T, E extends RuntimeException> void assertThat(T expect, Supplier<T> cond, E ex) {
        assertThat(expect, cond.get(), ex);
    }

    public static <T, E extends RuntimeException> void assertThat(T expect, T cond, E ex) {
        if (!equals(cond, expect)) {
            throw ex;
        }
    }

    public static <E extends RuntimeException> void assertFalse(Supplier<Boolean> cond, E ex) {
        assertThat(false, cond, ex);
    }

    public static <E extends RuntimeException> void assertTrue(Supplier<Boolean> cond, E ex) {
        assertThat(true, cond, ex);
    }

    public static <E extends RuntimeException> void assertFalse(Boolean cond, E ex) {
        assertThat(false, cond, ex);
    }

    public static <E extends RuntimeException> void assertTrue(Boolean cond, E ex) {
        assertThat(true, cond, ex);
    }


    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);

    public static boolean isIpAddress(String ip) {
        return addressPattern.matcher(ip).matches();
    }

    public static boolean isIpRange(String ip) {
        return cidrPattern.matcher(ip).matches();
    }

    public static boolean isIpInRange(String ip, List<String> list, BiFunction<Exception, String[], ? extends RuntimeException> func) {
        return Optional.ofNullable(list)
                .orElse(Lists.newArrayList()).stream()
                .filter(setIp -> {
                    try {
                        boolean result = isIpInRange(ip, isIpAddress(setIp)? setIp+"/32" : setIp);
                        return result;
                    } catch (Exception e) {
                        throw func.apply(e, new String[]{setIp, ip});
                    }
                })
                .findFirst().isPresent();
    }

    public static boolean isIpInRange(String ip, String range) {
        String[] info = range.split("/");
        int len = Integer.parseInt(info[1]);
        if (len < 1) { return true; }

        String dest = ipToBinaryString(info[0]);
        String src = ipToBinaryString(ip);
        return equals(dest.substring(0, len), src.substring(0, len));
    }

    public static String ipToBinaryString(String ipString) {
        String[] ipList = ipString.split("\\.");
        return Stream.of(ipList)
                .map(ip -> StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(ip)), 8, "0"))
                .reduce((s1, s2) -> string(s1, s2)).orElse(null);
    }
}
