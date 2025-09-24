package com.bank.cardmanager.service;

import com.bank.cardmanager.exception.*;
import com.bank.cardmanager.model.Card;
import com.bank.cardmanager.model.User;
import com.bank.cardmanager.repository.CardRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class CardService {
    
    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    
    public CardService(CardRepository cardRepository, EncryptionService encryptionService) {
        this.cardRepository = cardRepository;
        this.encryptionService = encryptionService;
    }
    
    public Card createCard(Card card, User user) {
        validateCardCreation(card);
        
        // Шифруем чувствительные данные
        card.setCardNumber(encryptionService.encrypt(card.getCardNumber()));
        card.setCvv(encryptionService.encrypt(card.getCvv()));
        card.setUser(user);
        
        // Проверяем срок действия
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(Card.CardStatus.EXPIRED);
        }
        
        return cardRepository.save(card);
    }
    
    private void validateCardCreation(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Card expiry date cannot be in the past");
        }
        
        if (StringUtils.isBlank(card.getCardHolder())) {
            throw new ValidationException("Card holder name is required");
        }
        
        // Проверка формата номера карты (Luhn algorithm)
        if (!isValidCardNumber(card.getCardNumber())) {
            throw new ValidationException("Invalid card number format");
        }
    }
    
    public Page<Card> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByUserAndStatusNot(user, Card.CardStatus.EXPIRED, pageable);
    }
    
    public Card getUserCard(Long cardId, User user) {
        return cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));
    }
    
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));
    }
    
    public Card blockCard(Long cardId, User user) {
        Card card = getUserCard(cardId, user);
        validateCardOperation(card, "block");
        
        card.setStatus(Card.CardStatus.BLOCKED);
        return cardRepository.save(card);
    }
    
    public Card activateCard(Long cardId, User user) {
        Card card = getUserCard(cardId, user);
        
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw CardOperationException.cardExpired(getMaskedCardNumber(card));
        }
        
        card.setStatus(Card.CardStatus.ACTIVE);
        return cardRepository.save(card);
    }
    
    public Card adminBlockCard(Long cardId) {
        Card card = getCardById(cardId);
        validateCardOperation(card, "admin block");
        
        card.setStatus(Card.CardStatus.BLOCKED);
        return cardRepository.save(card);
    }
    
    public Card adminActivateCard(Long cardId) {
        Card card = getCardById(cardId);
        
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw CardOperationException.cardExpired(getMaskedCardNumber(card));
        }
        
        card.setStatus(Card.CardStatus.ACTIVE);
        return cardRepository.save(card);
    }
    
    private void validateCardOperation(Card card, String operation) {
        if (card.getStatus() == Card.CardStatus.EXPIRED) {
            throw CardOperationException.cardExpired(getMaskedCardNumber(card));
        }
        
        if (card.getStatus() == Card.CardStatus.BLOCKED && "block".equals(operation)) {
            throw new BusinessException("Card is already blocked");
        }
    }
    
    public String getMaskedCardNumber(Card card) {
        String decryptedNumber = encryptionService.decrypt(card.getCardNumber());
        return maskCardNumber(decryptedNumber);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (StringUtils.isBlank(cardNumber) || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return String.format("**** **** **** %s", lastFour);
    }
    
    public boolean isCardOwnedByUser(Long cardId, User user) {
        return cardRepository.existsByIdAndUser(cardId, user);
    }
    
    public void validateCardForTransfer(Card card, BigDecimal amount) {
        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            throw CardOperationException.invalidCardStatus(
                getMaskedCardNumber(card), card.getStatus().name());
        }
        
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw CardOperationException.cardExpired(getMaskedCardNumber(card));
        }
        
        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                getMaskedCardNumber(card), card.getBalance(), amount);
        }
    }
    
    public void updateCardBalance(Card card, BigDecimal amount) {
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
    }
    
    private boolean isValidCardNumber(String cardNumber) {
        // Simplified Luhn algorithm check
        if (StringUtils.isBlank(cardNumber) || !cardNumber.matches("\\d+")) {
            return false;
        }
        
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}