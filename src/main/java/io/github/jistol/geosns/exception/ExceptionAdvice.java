package io.github.jistol.geosns.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Map<String, Object>> constraintViolation(ConstraintViolationException e) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = Maps.newHashMap();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            if (violation.getPropertyPath() != null) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }

        result.put("errors", errors);
        log.error(e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
