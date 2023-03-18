package com.klid.demo_spring_webflux_webclient.service.rest;

import org.springframework.http.HttpStatus;

/**
 * @author Ivan Kaptue
 */
public class PostApiException extends RuntimeException {

    private HttpStatus status;
    private String responseBody;

    public PostApiException(String message, Throwable t) {
        super(message, t);
    }

    public PostApiException(String message, HttpStatus status, String responseBody, Throwable throwable) {
        super(message, throwable);
        this.status = status;
        this.responseBody = responseBody;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
