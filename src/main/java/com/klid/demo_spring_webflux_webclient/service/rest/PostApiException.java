package com.klid.demo_spring_webflux_webclient.service.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

/**
 * @author Ivan Kaptue
 */
public class PostApiException extends RuntimeException {

    private HttpStatus status;
    private String responseBody;

    public PostApiException(String message, Throwable t) {
        super(message, t);
    }

    public PostApiException(String message, @NonNull HttpStatusCode status, String responseBody, Throwable throwable) {
        super(message, throwable);
        this.status = (HttpStatus) status;
        this.responseBody = responseBody;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
