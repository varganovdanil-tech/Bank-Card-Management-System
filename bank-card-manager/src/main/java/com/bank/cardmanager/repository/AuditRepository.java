package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByEntityName(String entityName, Pageable pageable);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    Page<AuditLog> findByUsername(String username, Pageable pageable);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityName IS NULL OR a.entityName = :entityName) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:username IS NULL OR a.username = :username) AND " +
           "a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    Page<AuditLog> findByCriteria(@Param("entityName") String entityName,
                                 @Param("action") String action,
                                 @Param("username") String username,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);
}