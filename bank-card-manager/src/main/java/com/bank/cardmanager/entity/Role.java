package com.bank.cardmanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name;
    
    public Role() {}
    
    public Role(RoleName name) {
        this.name = name;
    }
    
    public enum RoleName {
        ROLE_USER, ROLE_ADMIN
    }
    
    // Getters and setters...
}