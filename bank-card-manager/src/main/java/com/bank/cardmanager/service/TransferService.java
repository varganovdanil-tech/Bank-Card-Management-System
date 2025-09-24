package com.bank.cardmanager.service;

import com.bank.cardmanager.dto.TransferRequest;
import com.bank.cardmanager.exception.*;
import com.bank.cardmanager.model.Card;
import com.bank.cardmanager.model.Transaction;
import com.bank.cardmanager.model.User;
import com.bank.cardmanager.repository.CardRepository;
import com.bank.cardmanager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class TransferService {
    
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final UserService userService;
    
    public TransferService(CardRepository cardRepository, 
                          TransactionRepository transactionRepository,
                          CardService cardService,
                          UserService userService) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.cardService = cardService;
        this.userService = userService;
    }
    
    public Transaction transferBetweenOwnCards(TransferRequest request, String username) {
        User user = userService.getUserByUsername(username);
        
        // Проверяем принадлежность карт пользователю
        if (!cardService.isCardOwnedByUser(request.getFromCardId(), user)) {
            throw new SecurityException("Source card does not belong to the user");
        }
        
        if (!cardService.isCardOwnedByUser(request.getToCardId(), user)) {
            throw new SecurityException("Destination card does not belong to the user");
        }
        
        Card fromCard = cardService.getCardById(request.getFromCardId());
        Card toCard = cardService.getCardById(request.getToCardId());
        
        // Валидация перевода
        validateTransfer(fromCard, toCard, request.getAmount());
        
        // Выполняем перевод
        return executeTransfer(fromCard, toCard, request.getAmount(), request.getDescription());
    }
    
    private void validateTransfer(Card fromCard, Card toCard, BigDecimal amount) {
        // Проверка исходной карты
        cardService.validateCardForTransfer(fromCard, amount);
        
        // Проверка целевой карты
        if (toCard.getStatus() != Card.CardStatus.ACTIVE) {
            throw CardOperationException.invalidCardStatus(
                cardService.getMaskedCardNumber(toCard), toCard.getStatus().name());
        }
        
        if (toCard.getExpiryDate().isBefore(java.time.LocalDate.now())) {
            throw CardOperationException.cardExpired(cardService.getMaskedCardNumber(toCard));
        }
        
        // Проверка валюты (если поддерживаются разные валюты)
        // if (!fromCard.getCurrency().equals(toCard.getCurrency())) {
        //     throw new BusinessException("Currency mismatch between cards");
        // }
        
        // Проверка минимальной и максимальной суммы
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transfer amount must be positive");
        }
        
        if (amount.compareTo(new BigDecimal("1000000")) > 0) { // Пример лимита
            throw new ValidationException("Transfer amount exceeds maximum limit");
        }
    }
    
    private Transaction executeTransfer(Card fromCard, Card toCard, BigDecimal amount, String description) {
        try {
            // Снимаем средства с исходной карты
            cardService.updateCardBalance(fromCard, amount.negate());
            
            // Зачисляем средства на целевую карту
            cardService.updateCardBalance(toCard, amount);
            
            // Создаем запись о транзакции
            Transaction transaction = new Transaction();
            transaction.setFromCard(fromCard);
            transaction.setToCard(toCard);
            transaction.setAmount(amount);
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setDescription(description != null ? description : "Transfer between own cards");
            transaction.setTransactionDate(java.time.LocalDateTime.now());
            
            return transactionRepository.save(transaction);
            
        } catch (Exception e) {
            // В случае ошибки откатываем транзакцию
            throw new BusinessException("Transfer failed: " + e.getMessage(), e);
        }
    }
}