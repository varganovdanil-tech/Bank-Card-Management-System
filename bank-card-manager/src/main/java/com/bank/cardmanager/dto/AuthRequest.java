package com.bank.cardmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    // Getters and setters...
}