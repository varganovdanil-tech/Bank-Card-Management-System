package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.Transaction;
import com.bank.cardmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Операции для пользователя
    @Query("SELECT t FROM Transaction t WHERE " +
           "t.fromCard.user = :user OR t.toCard.user = :user " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromCard.user = :user OR t.toCard.user = :user) AND " +
           "t.status = :status " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByUserAndStatus(@Param("user") User user, 
                                         @Param("status") Transaction.TransactionStatus status,
                                         Pageable pageable);
    
    // Операции по карте
    Page<Transaction> findByFromCardId(Long fromCardId, Pageable pageable);
    
    Page<Transaction> findByToCardId(Long toCardId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "t.fromCard.id = :cardId OR t.toCard.id = :cardId " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByCardId(@Param("cardId") Long cardId, Pageable pageable);
    
    // Операции по статусу
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    // Операции по дате
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    List<Transaction> findByTransactionDateBefore(LocalDateTime date);
    
    List<Transaction> findByTransactionDateAfter(LocalDateTime date);
    
    // Операции по сумме
    List<Transaction> findByAmountGreaterThanEqual(Double minAmount);
    
    List<Transaction> findByAmountLessThanEqual(Double maxAmount);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountBetween(@Param("minAmount") Double minAmount, 
                                         @Param("maxAmount") Double maxAmount);
    
    // Комплексные запросы
    @Query("SELECT t FROM Transaction t WHERE " +
           "t.fromCard.user.id = :userId AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findOutgoingTransactionsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "t.toCard.user.id = :userId AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findIncomingTransactionsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Статистические запросы
    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "(t.fromCard.user.id = :userId OR t.toCard.user.id = :userId) AND " +
           "t.status = 'COMPLETED'")
    long countCompletedTransactionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.fromCard.user.id = :userId AND " +
           "t.status = 'COMPLETED' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalOutgoingAmountByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.toCard.user.id = :userId AND " +
           "t.status = 'COMPLETED' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalIncomingAmountByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Поиск с фильтрами
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:fromCardId IS NULL OR t.fromCard.id = :fromCardId) AND " +
           "(:toCardId IS NULL OR t.toCard.id = :toCardId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR t.amount <= :maxAmount) AND " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByFilters(@Param("fromCardId") Long fromCardId,
                                   @Param("toCardId") Long toCardId,
                                   @Param("status") Transaction.TransactionStatus status,
                                   @Param("minAmount") Double minAmount,
                                   @Param("maxAmount") Double maxAmount,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);
    
    // Для отчетов
    @Query("SELECT FUNCTION('DATE', t.transactionDate) as transactionDate, " +
           "COUNT(t) as transactionCount, " +
           "SUM(t.amount) as totalAmount " +
           "FROM Transaction t " +
           "WHERE t.status = 'COMPLETED' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', t.transactionDate) " +
           "ORDER BY transactionDate DESC")
    List<Object[]> getDailyTransactionStats(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}