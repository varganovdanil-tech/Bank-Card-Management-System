package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(Role.RoleName name);
    
    Boolean existsByName(Role.RoleName name);
    
    long count();
}