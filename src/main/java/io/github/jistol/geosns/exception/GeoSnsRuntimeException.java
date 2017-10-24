package io.github.jistol.geosns.exception;

import io.github.jistol.geosns.type.Code;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GeoSnsRuntimeException extends RuntimeException {
    private final String code;

    public GeoSnsRuntimeException(Code code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public GeoSnsRuntimeException(Code code, String message) {
        super(message);
        this.code = code.getCode();
    }
}
