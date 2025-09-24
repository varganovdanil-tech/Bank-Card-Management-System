package com.bank.cardmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CardOperationException extends BusinessException {
    
    public CardOperationException(String message) {
        super(message);
    }
    
    public CardOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static CardOperationException cardExpired(String cardNumber) {
        return new CardOperationException("Card %s has expired and cannot be used for operations", cardNumber);
    }
    
    public static CardOperationException cardBlocked(String cardNumber) {
        return new CardOperationException("Card %s is blocked and cannot be used for operations", cardNumber);
    }
    
    public static CardOperationException invalidCardStatus(String cardNumber, String currentStatus) {
        return new CardOperationException("Card %s has invalid status: %s", cardNumber, currentStatus);
    }
}