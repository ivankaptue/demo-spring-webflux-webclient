package com.klid.demo_spring_webflux_webclient.service.rest;

public class RequestNotMadeException extends PostApiException {

    private final String localizedMessage;

    public RequestNotMadeException(String message, String localizedMessage, Throwable t) {
        super(message, t);
        this.localizedMessage = localizedMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return localizedMessage;
    }
}
