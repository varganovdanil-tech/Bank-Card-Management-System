package com.bank.cardmanager.controller;

import com.bank.cardmanager.model.Card;
import com.bank.cardmanager.model.User;
import com.bank.cardmanager.service.CardService;
import com.bank.cardmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Card Management", description = "APIs for managing bank cards")
public class CardController {
    
    private final CardService cardService;
    private final UserService userService;
    
    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new card (Admin only)")
    public ResponseEntity<Card> createCard(@RequestBody Card card, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        Card createdCard = cardService.createCard(card, user);
        return ResponseEntity.ok(createdCard);
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get user's cards with pagination")
    public ResponseEntity<Page<Card>> getUserCards(@ParameterObject Pageable pageable, 
                                                  Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Page<Card> cards = cardService.getUserCards(user, pageable);
        
        // Маскируем номера карт для ответа
        cards.forEach(card -> {
            String maskedNumber = cardService.getMaskedCardNumber(card);
            // Можно добавить DTO для безопасного отображения
        });
        
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{cardId}")
    @Operation(summary = "Get specific card details")
    public ResponseEntity<Card> getCard(@PathVariable Long cardId, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Card card = cardService.getUserCard(cardId, user);
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{cardId}/block")
    @Operation(summary = "Block user's card")
    public ResponseEntity<Card> blockCard(@PathVariable Long cardId, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Card blockedCard = cardService.blockCard(cardId, user);
        return ResponseEntity.ok(blockedCard);
    }
    
    @PutMapping("/{cardId}/activate")
    @Operation(summary = "Activate user's card")
    public ResponseEntity<Card> activateCard(@PathVariable Long cardId, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Card activatedCard = cardService.activateCard(cardId, user);
        return ResponseEntity.ok(activatedCard);
    }
    
    @PutMapping("/admin/{cardId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Block any card")
    public ResponseEntity<Card> adminBlockCard(@PathVariable Long cardId) {
        Card blockedCard = cardService.adminBlockCard(cardId);
        return ResponseEntity.ok(blockedCard);
    }
    
    @PutMapping("/admin/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Activate any card")
    public ResponseEntity<Card> adminActivateCard(@PathVariable Long cardId) {
        Card activatedCard = cardService.adminActivateCard(cardId);
        return ResponseEntity.ok(activatedCard);
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all cards (Admin only)")
    public ResponseEntity<Page<Card>> getAllCards(@ParameterObject Pageable pageable) {
        Page<Card> cards = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }
}