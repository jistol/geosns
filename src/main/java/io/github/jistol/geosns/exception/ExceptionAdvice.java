package io.github.jistol.geosns.exception;

import com.google.common.collect.Maps;
import io.github.jistol.geosns.type.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.*;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ResponseBody
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Map<String, Object>> constraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = Maps.newHashMap();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            if (violation.getPropertyPath() != null) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        log.error("ConstraintViolationException - msg : {}, errors : {}", e.getMessage(), errors);
        return ResponseEntity.badRequest().body(map(entry("code", Code.etcError), entry("msg", e.getMessage()), entry("errors", errors)));
    }

    @ResponseBody
    @ExceptionHandler({ GeoSnsRestRuntimeException.class })
    public ResponseEntity<Map<String, Object>> restError(GeoSnsRestRuntimeException e) {
        log.error("GeoSnsRestRuntimeException - {}", e.toResultMap());
        return ResponseEntity.badRequest().body(e.toResultMap());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<String> error(Exception e) {
        log.error("{} - {}", e.getClass().getName(), e.getMessage());
        log.error("stacktrace : ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
