package com.bank.cardmanager.model.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String entityName;
    private Long entityId;
    private String action; // CREATE, UPDATE, DELETE
    private String oldValue;
    private String newValue;
    private String username;
    private LocalDateTime timestamp;
    private String ipAddress;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    // Constructors, getters, setters
}