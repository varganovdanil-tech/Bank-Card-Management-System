package com.bank.cardmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String cardNumber; // Зашифрованное значение
    
    @NotBlank
    private String cardHolder;
    
    @Future
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;
    
    @DecimalMin("0.00")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @NotBlank
    private String cvv; // Зашифрованное значение
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum CardStatus {
        ACTIVE, BLOCKED, EXPIRED
    }
    
    // Constructors, getters, setters...
}