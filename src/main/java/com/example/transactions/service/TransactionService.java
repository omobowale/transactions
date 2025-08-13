package com.example.transactions.service;

import com.example.transactions.model.Bucket;
import com.example.transactions.model.StatisticsResponse;
import com.example.transactions.model.Transaction;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class TransactionService {
    private static final int INTERVAL = 30;
    private final Bucket[] buckets = new Bucket[INTERVAL];
    private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());

    public TransactionService() {
        for (int i = 0; i < INTERVAL; i++) {
            buckets[i] = new Bucket();
        }
    }

    public List<Transaction> getAllTransactions() {
        Instant now = Instant.now();
        return transactions.stream()
                .filter(t -> Duration.between(t.getTimestamp(), now).toMillis() < 30000)
                .toList();
    }

    public synchronized boolean addTransaction(double amount, Instant timestamp) {
        long nowMillis = Instant.now().toEpochMilli();
        long tsMillis = timestamp.toEpochMilli();

        if (tsMillis < nowMillis - INTERVAL * 1000) {
            return false; // too old
        }
        if (tsMillis > nowMillis) {
            throw new IllegalArgumentException("Future timestamp");
        }

        long epochSecond = tsMillis / 1000;
        int index = (int) (epochSecond % INTERVAL);
        Bucket bucket = buckets[index];

        if (bucket.getTimestamp() != epochSecond) {
            bucket.reset(epochSecond);
        }

        bucket.add(amount);
        transactions.add(new Transaction(amount, timestamp)); // Keep for GET
        return true;
    }

    public synchronized StatisticsResponse getStatistics() {
        long now = Instant.now().getEpochSecond();

        double sum = 0;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        long count = 0;

        for (Bucket bucket : buckets) {
            if (now - bucket.getTimestamp() < INTERVAL) {
                sum += bucket.getSum();
                max = Math.max(max, bucket.getMax());
                min = Math.min(min, bucket.getMin());
                count += bucket.getCount();
            }
        }

        double avg = count == 0 ? 0 : sum / count;
        if (count == 0) {
            max = 0;
            min = 0;
        }

        return new StatisticsResponse(sum, avg, max, min, count);
    }

    public synchronized void deleteAll() {
        for (Bucket bucket : buckets) {
            bucket.reset(0);
        }
    }
}