package com.teya.gustavo.interview.tiny_ledger.controller;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.service.TransactionService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> recordTransaction(@RequestBody TransactionRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("amount must be greater than zero");
        }
        if (request.type() == null) {
            return ResponseEntity.badRequest().body("transactionType must not be null");
        }

        return transactionService.recordTransaction(request.type(), request.amount())
                .<ResponseEntity<?>>map(t -> ResponseEntity.status(HttpStatusCode.valueOf(201)).body(t))
                .orElse(ResponseEntity.status(HttpStatusCode.valueOf(422)).body("insufficient funds"));
    }

    @GetMapping("/balance")
    public BigDecimal getCurrentBalance() {
        return transactionService.getCurrentBalance();
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}
