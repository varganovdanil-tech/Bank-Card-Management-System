package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.Card;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CardRepositoryImpl implements CardRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Card> findExpiredCards() {
        String jpql = "SELECT c FROM Card c WHERE c.expiryDate < :today AND c.status <> 'EXPIRED'";
        TypedQuery<Card> query = entityManager.createQuery(jpql, Card.class);
        query.setParameter("today", LocalDate.now());
        return query.getResultList();
    }
    
    @Override
    public void bulkUpdateCardStatus(List<Long> cardIds, Card.CardStatus newStatus) {
        if (cardIds == null || cardIds.isEmpty()) {
            return;
        }
        
        String jpql = "UPDATE Card c SET c.status = :newStatus WHERE c.id IN :cardIds";
        entityManager.createQuery(jpql)
                .setParameter("newStatus", newStatus)
                .setParameter("cardIds", cardIds)
                .executeUpdate();
        
        entityManager.flush();
        entityManager.clear();
    }
    
    @Override
    public List<Object[]> getCardStatisticsByUser() {
        String jpql = "SELECT c.user.id, " +
                     "COUNT(c), " +
                     "SUM(c.balance), " +
                     "AVG(c.balance), " +
                     "MIN(c.balance), " +
                     "MAX(c.balance) " +
                     "FROM Card c " +
                     "WHERE c.status = 'ACTIVE' " +
                     "GROUP BY c.user.id";
        
        return entityManager.createQuery(jpql, Object[].class).getResultList();
    }
}