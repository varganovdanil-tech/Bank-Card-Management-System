package com.bank.cardmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityException extends RuntimeException {
    
    public SecurityException(String message) {
        super(message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static SecurityException accessDenied(String resource, String operation) {
        return new SecurityException("Access denied to %s for operation: %s", resource, operation);
    }
}