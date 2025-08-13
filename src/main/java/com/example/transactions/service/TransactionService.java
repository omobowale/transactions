package com.example.transactions.service;

import com.example.transactions.model.Bucket;
import com.example.transactions.model.StatisticsResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionService {
    private static final int INTERVAL = 30;
    private final Bucket[] buckets = new Bucket[INTERVAL];

    public TransactionService() {
        for (int i = 0; i < INTERVAL; i++) {
            buckets[i] = new Bucket();
        }
    }

    public synchronized boolean addTransaction(double amount, long timestampMillis) {
        long now = Instant.now().toEpochMilli();
        if (timestampMillis < now - INTERVAL * 1000 || timestampMillis > now) {
            return false; // too old or future
        }

        long epochSecond = timestampMillis / 1000;
        int index = (int) (epochSecond % INTERVAL);
        Bucket bucket = buckets[index];

        if (bucket.getTimestamp() != epochSecond) {
            bucket.reset(epochSecond);
        }
        bucket.add(amount);
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
