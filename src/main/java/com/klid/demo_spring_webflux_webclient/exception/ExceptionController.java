package com.klid.demo_spring_webflux_webclient.exception;

import com.klid.demo_spring_webflux_webclient.service.rest.PostApiException;
import com.klid.demo_spring_webflux_webclient.service.rest.RequestNotMadeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.NoSuchElementException;

/**
 * @author Ivan Kaptue
 */
@ControllerAdvice
public class ExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(PostApiException.class)
    public ResponseEntity<String> handlePostApiException(PostApiException ex) {
        logger.info("Response body : " + ex.getResponseBody());

        var status = ex.getStatus();
        if (ex.getCause() instanceof PostApiException e) {
            status = e.getStatus();
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable. Code : " + status);
    }

    @ExceptionHandler(RequestNotMadeException.class)
    public ResponseEntity<String> handleRequestNotMadeException(RequestNotMadeException ex) {
        if (ex.getCause() instanceof WebClientRequestException e) {
            logger.info("Localized message : %s".formatted(e.getLocalizedMessage()));
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Cannot contact server. Code : " + HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found. Code : " + HttpStatus.NOT_FOUND);
    }
}
