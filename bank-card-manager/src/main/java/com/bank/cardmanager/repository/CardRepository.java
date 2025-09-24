package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.Card;
import com.bank.cardmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    // Базовые операции для пользователя
    Page<Card> findByUser(User user, Pageable pageable);
    
    List<Card> findByUser(User user);
    
    Page<Card> findByUserAndStatus(User user, Card.CardStatus status, Pageable pageable);
    
    Page<Card> findByUserAndStatusNot(User user, Card.CardStatus status, Pageable pageable);
    
    Optional<Card> findByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
    
    long countByUser(User user);
    
    // Операции по статусу
    Page<Card> findByStatus(Card.CardStatus status, Pageable pageable);
    
    List<Card> findByStatus(Card.CardStatus status);
    
    Page<Card> findByStatusNot(Card.CardStatus status, Pageable pageable);
    
    long countByStatus(Card.CardStatus status);
    
    // Операции с датой истечения
    List<Card> findByExpiryDateBefore(LocalDate date);
    
    List<Card> findByExpiryDateBeforeAndStatus(LocalDate date, Card.CardStatus status);
    
    @Query("SELECT c FROM Card c WHERE c.expiryDate BETWEEN :startDate AND :endDate")
    List<Card> findCardsExpiringBetween(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    // Поиск по владельцу карты
    List<Card> findByCardHolderContainingIgnoreCase(String cardHolder);
    
    Page<Card> findByCardHolderContainingIgnoreCase(String cardHolder, Pageable pageable);
    
    // Операции с балансом
    List<Card> findByBalanceGreaterThanEqual(Double minBalance);
    
    List<Card> findByBalanceLessThanEqual(Double maxBalance);
    
    @Query("SELECT c FROM Card c WHERE c.balance BETWEEN :minBalance AND :maxBalance")
    List<Card> findByBalanceBetween(@Param("minBalance") Double minBalance, 
                                   @Param("maxBalance") Double maxBalance);
    
    // Административные запросы
    @Query("SELECT c FROM Card c JOIN c.user u WHERE u.active = true")
    Page<Card> findCardsOfActiveUsers(Pageable pageable);
    
    @Query("SELECT c FROM Card c JOIN c.user u WHERE u.active = false")
    Page<Card> findCardsOfInactiveUsers(Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.status = :status")
    List<Card> findByUserIdAndStatus(@Param("userId") Long userId, 
                                    @Param("status") Card.CardStatus status);
    
    // Статистические запросы
    @Query("SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.balance) FROM Card c WHERE c.user.id = :userId")
    Double getTotalBalanceByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(c.balance) FROM Card c WHERE c.status = 'ACTIVE'")
    Double getAverageBalanceOfActiveCards();
    
    // Поиск карт с пагинацией и сортировкой
    @Query("SELECT c FROM Card c WHERE " +
           "(:cardHolder IS NULL OR LOWER(c.cardHolder) LIKE LOWER(CONCAT('%', :cardHolder, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:minBalance IS NULL OR c.balance >= :minBalance) AND " +
           "(:maxBalance IS NULL OR c.balance <= :maxBalance)")
    Page<Card> findByCriteria(@Param("cardHolder") String cardHolder,
                             @Param("status") Card.CardStatus status,
                             @Param("minBalance") Double minBalance,
                             @Param("maxBalance") Double maxBalance,
                             Pageable pageable);
    
    // Блокировка для обновления баланса
    @Query("SELECT c FROM Card c WHERE c.id = :cardId")
    Optional<Card> findByIdForUpdate(@Param("cardId") Long cardId);
}