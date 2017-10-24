package io.github.jistol.geosns.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

public class Cast {
    public static final String string(Object... args)
    {
        StringBuilder sb = new StringBuilder();
        for(Object str : args)
        {
            sb.append(str);
        }
        return sb.toString();
    }

    public static final String strIfAbsent(String str, String defaultStr) {
        return (str == null || str.trim().length() < 1)? defaultStr : str;
    }

    public static final <T> T[] array(T ... args)
    {
        return args;
    }

    public static final <T> List<T> list(T ... args)
    {
        return list(new ArrayList<T>(), args);
    }

    public static final <T> List<T> list(List<T> list, T ... args)
    {
        for(T item : args)
        {
            list.add(item);
        }
        return list;
    }

    public static final <K,V> Map<K,V> map(Map<K,V> map, Map.Entry<K, V>... keyValues)
    {
        map.putAll(map(keyValues));
        return map;
    }

    public static final <K,V> Map<K,V> map(Map.Entry<K, V>... keyValues)
    {
        Map<K,V> map = new HashMap<K,V>();
        for(Map.Entry<K, V> item : keyValues)
        {
            map.put(item.getKey(), item.getValue());
        }
        return map;
    }

    public static final <K,V> Map.Entry<K,V> entry(K key, V value)
    {
        return new AbstractMap.SimpleEntry<K,V>(key, value);
    }

    public static final int integer(Object obj)
    {
        int iSeq = 0;
        if (obj instanceof Number)
        {
            iSeq = ((Number) obj).intValue();
        }
        else
        {
            iSeq = Integer.parseInt(obj.toString());
        }
        return iSeq;
    }

    public static final String url(String url, Map<String, Object> param) throws UnsupportedEncodingException {
        return url(url, param, "UTF-8");
    }

    public static final String url(String url, Map<String, Object> param, String encoding) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (param != null) {
            Object[] keys = param.keySet().toArray();
            for (int i=0, ilen=keys.length ; i<ilen ; i++) {
                sb.append(i==0?"?":"&").append(keys[i]).append("=").append(URLEncoder.encode(param.get(keys[i]).toString(), encoding));
            }
        }
        return sb.toString();
    }

    public static final String url(String url, Map.Entry<String, Object> entry) throws UnsupportedEncodingException {
        return url(url, map(entry));
    }

    public static final String redirect(String url) {
        return "redirect:" + url;
    }

    public static final String redirect(String url, Map<String, Object> param) throws UnsupportedEncodingException {
        return "redirect:" + url(url, param);
    }

    public static final String redirect(String url, Map.Entry<String, Object> entry) throws UnsupportedEncodingException {
        return redirect(url, map(entry));
    }

    public static final Cookie cookie(HttpServletRequest req, String name) {
        for (Cookie c : req.getCookies()) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public static final String msgFormat(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }
}
