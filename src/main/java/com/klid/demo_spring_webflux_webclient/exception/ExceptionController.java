package com.klid.demo_spring_webflux_webclient.exception;

import com.klid.demo_spring_webflux_webclient.rest.PostApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Ivan Kaptue
 */
@ControllerAdvice
public class ExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<String> handlePostApiException(PostApiException ex) {
        logger.info("Response body : " + ex.getResponseBody());

        var status = ex.getStatus();
        if (ex.getCause() instanceof PostApiException) {
            var e = (PostApiException) ex.getCause();
            status = e.getStatus();
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable. Code : " + status);
    }
}
