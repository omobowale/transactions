package com.example.transactions.controller;

import com.example.transactions.model.Transaction;
import com.example.transactions.service.TransactionService;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping
    public ResponseEntity<Void> addTransaction(@RequestBody Transaction transaction) {
        try {
            Instant timestamp = transaction.getTimestamp();
            if (timestamp == null)
                return ResponseEntity.badRequest().build();

            boolean added = transactionService.addTransaction(transaction.getAmount(), timestamp);
            if (added) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                long nowMillis = Instant.now().toEpochMilli();
                long tsMillis = timestamp.toEpochMilli();
                if (tsMillis < nowMillis - 30000) {
                    return ResponseEntity.noContent().build(); // 204 older than 30s
                } else {
                    return ResponseEntity.unprocessableEntity().build(); // 422 future
                }
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTransactions() {
        transactionService.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
