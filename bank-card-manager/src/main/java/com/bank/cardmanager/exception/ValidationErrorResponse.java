package com.bank.cardmanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;
    
    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, 
                                  String path, Map<String, String> fieldErrors) {
        super(status, message, timestamp, path);
        this.fieldErrors = fieldErrors;
    }
    
    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }
}