package com.meexora.authservice.exception;

public class EmailRequestRateLimitException extends RuntimeException{
    public EmailRequestRateLimitException(String message) {
        super(message);
    }
}
