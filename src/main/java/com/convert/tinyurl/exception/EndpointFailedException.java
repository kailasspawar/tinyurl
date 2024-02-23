package com.convert.tinyurl.exception;

public class EndpointFailedException extends RuntimeException {
    public EndpointFailedException(String message) {
        super(message);
    }
}
