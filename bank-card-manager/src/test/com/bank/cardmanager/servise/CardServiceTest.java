package com.bank.cardmanager.service;

import com.bank.cardmanager.model.Card;
import com.bank.cardmanager.model.User;
import com.bank.cardmanager.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    
    @Mock
    private CardRepository cardRepository;
    
    @Mock
    private EncryptionService encryptionService;
    
    @InjectMocks
    private CardService cardService;
    
    @Test
    void createCard_ShouldEncryptSensitiveData() {
        // Given
        User user = new User();
        Card card = new Card();
        card.setCardNumber("1234567890123456");
        card.setCvv("123");
        
        when(encryptionService.encrypt(any())).thenReturn("encrypted");
        when(cardRepository.save(any())).thenReturn(card);
        
        // When
        Card result = cardService.createCard(card, user);
        
        // Then
        verify(encryptionService).encrypt("1234567890123456");
        verify(encryptionService).encrypt("123");
        assertNotNull(result);
    }
}