package com.bank.cardmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends BusinessException {
    
    public InsufficientFundsException(String cardNumber, BigDecimal currentBalance, BigDecimal requiredAmount) {
        super("Insufficient funds on card %s. Current balance: %.2f, Required: %.2f", 
              cardNumber, currentBalance, requiredAmount);
    }
    
    public InsufficientFundsException(String message) {
        super(message);
    }
}