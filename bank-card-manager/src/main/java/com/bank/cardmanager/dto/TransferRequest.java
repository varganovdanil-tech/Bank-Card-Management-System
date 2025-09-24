package com.bank.cardmanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {
    
    @NotNull
    private Long fromCardId;
    
    @NotNull
    private Long toCardId;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    private String description;
    
    // Getters and setters...
}