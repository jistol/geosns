package io.github.jistol.geosns.exception;

import io.github.jistol.geosns.type.Code;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@Getter
@RequiredArgsConstructor
public class GeoSnsRestRuntimeException extends RuntimeException {
    private final String code;

    public GeoSnsRestRuntimeException(Exception e) {
        super(e);
        code = Code.etcError.code;
    }

    public GeoSnsRestRuntimeException(Code code) {
        super(code.message);
        this.code = code.code;
    }

    public GeoSnsRestRuntimeException(Code code, String message) {
        super(message);
        this.code = code.code;
    }

    public Map<String, Object> toResultMap() {
        return map(entry("code", this.code), entry("msg", this.getMessage()));
    }

    public static Function<Exception, ? extends GeoSnsRestRuntimeException> func() {
        return e -> new GeoSnsRestRuntimeException(e);
    }

    public static Function<Code, ? extends GeoSnsRestRuntimeException> funcByCode() {
        return code -> new GeoSnsRestRuntimeException(code);
    }
}
