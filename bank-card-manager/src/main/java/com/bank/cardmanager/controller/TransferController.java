package com.bank.cardmanager.controller;

import com.bank.cardmanager.dto.TransferRequest;
import com.bank.cardmanager.model.Transaction;
import com.bank.cardmanager.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {
    
    private final TransferService transferService;
    
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    
    @PostMapping
    @Operation(summary = "Transfer between user's cards")
    public ResponseEntity<Transaction> transferBetweenCards(@RequestBody TransferRequest request,
                                                           Authentication authentication) {
        Transaction transaction = transferService.transferBetweenOwnCards(
            request, authentication.getName());
        return ResponseEntity.ok(transaction);
    }
}