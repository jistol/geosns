package io.github.jistol.geosns.util;

import io.github.jistol.geosns.type.Code;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

public class Result {
    public static ResponseEntity<Map<String, Object>> success(Map.Entry... entries) {
        Map<String, Object> map = map(entries);
        return ResponseEntity.ok(map(map, entry("code", Code.success.getCode()), entry("msg", Code.success.getMessage())));
    }

    public static ResponseEntity<Map<String, Object>> byCode(HttpStatus status, Code code) {
        return ResponseEntity.status(status).body(map(entry("code", code.getCode()), entry("msg", code.getMessage())));
    }

    public static ResponseEntity<Map<String, Object>> badRequest(Code code) {
        return byCode(HttpStatus.BAD_REQUEST, code);
    }
}
