package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByActiveTrue();
    
    List<User> findByActiveFalse();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE u.active = true AND u.id = :userId")
    Optional<User> findActiveUserById(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.active = true")
    boolean existsByUsernameAndActiveTrue(@Param("username") String username);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
}