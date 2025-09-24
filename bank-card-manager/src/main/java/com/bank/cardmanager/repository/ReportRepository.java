package com.bank.cardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Object, Long> {
    
    @Query(value = 
        "SELECT u.username, " +
        "COUNT(c.id) as card_count, " +
        "SUM(c.balance) as total_balance, " +
        "COUNT(t.id) as transaction_count, " +
        "SUM(CASE WHEN t.from_card_id = c.id THEN t.amount ELSE 0 END) as total_sent, " +
        "SUM(CASE WHEN t.to_card_id = c.id THEN t.amount ELSE 0 END) as total_received " +
        "FROM users u " +
        "LEFT JOIN cards c ON u.id = c.user_id " +
        "LEFT JOIN transactions t ON c.id = t.from_card_id OR c.id = t.to_card_id " +
        "WHERE u.active = true " +
        "GROUP BY u.id, u.username " +
        "ORDER BY total_balance DESC", 
        nativeQuery = true)
    List<Object[]> getUserFinancialReport();
    
    @Query(value =
        "SELECT DATE(t.transaction_date) as transaction_day, " +
        "COUNT(t.id) as daily_count, " +
        "SUM(t.amount) as daily_amount, " +
        "AVG(t.amount) as avg_amount " +
        "FROM transactions t " +
        "WHERE t.status = 'COMPLETED' AND " +
        "t.transaction_date BETWEEN :startDate AND :endDate " +
        "GROUP BY DATE(t.transaction_date) " +
        "ORDER BY transaction_day DESC",
        nativeQuery = true)
    List<Object[]> getDailyTransactionReport(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
}