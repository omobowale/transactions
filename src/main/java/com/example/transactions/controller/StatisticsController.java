package com.example.transactions.controller;

import com.example.transactions.model.StatisticsResponse;
import com.example.transactions.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final TransactionService transactionService;

    public StatisticsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public StatisticsResponse getStatistics() {
        return transactionService.getStatistics();
    }
}
