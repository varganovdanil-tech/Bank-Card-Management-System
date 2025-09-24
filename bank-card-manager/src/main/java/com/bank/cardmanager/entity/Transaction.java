package com.bank.cardmanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id")
    private Card fromCard;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id")
    private Card toCard;
    
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private String description;
    private LocalDateTime transactionDate;
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    // Constructors, getters, setters...
}