package com.bank.cardmanager.repository;

import com.bank.cardmanager.model.Card;
import java.util.List;

public interface CardRepositoryCustom {
    
    List<Card> findExpiredCards();
    
    void bulkUpdateCardStatus(List<Long> cardIds, Card.CardStatus newStatus);
    
    List<Object[]> getCardStatisticsByUser();
}