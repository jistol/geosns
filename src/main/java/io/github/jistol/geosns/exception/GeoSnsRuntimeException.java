package io.github.jistol.geosns.exception;

import io.github.jistol.geosns.type.Code;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class GeoSnsRuntimeException extends RuntimeException {
    private final String code;

    public GeoSnsRuntimeException(Exception e) {
        super(e);
        code = Code.etcError.getCode();
    }

    public GeoSnsRuntimeException(Code code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public GeoSnsRuntimeException(Code code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public static Function<Exception, ? extends GeoSnsRuntimeException> func() {
        return e -> new GeoSnsRuntimeException(e);
    }
}
