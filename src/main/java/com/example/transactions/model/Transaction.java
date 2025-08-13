package com.example.transactions.model;

import java.time.Instant;

public class Transaction {
    private double amount;
    private Instant timestamp; // epoch millis

    public Transaction() {}

    public Transaction(double amount, Instant timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
