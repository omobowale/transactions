package com.example.transactions.model;

public class Bucket {
    private double sum;
    private double max;
    private double min;
    private long count;
    private long timestamp; // epoch seconds

    public Bucket() {
        this.sum = 0;
        this.max = Double.NEGATIVE_INFINITY;
        this.min = Double.POSITIVE_INFINITY;
        this.count = 0;
        this.timestamp = 0;
    }

    public void reset(long timestamp) {
        this.sum = 0;
        this.max = Double.NEGATIVE_INFINITY;
        this.min = Double.POSITIVE_INFINITY;
        this.count = 0;
        this.timestamp = timestamp;
    }

    public double getSum() { return sum; }
    public double getMax() { return max; }
    public double getMin() { return min; }
    public long getCount() { return count; }
    public long getTimestamp() { return timestamp; }

    public void add(double amount) {
        this.sum += amount;
        this.max = Math.max(this.max, amount);
        this.min = Math.min(this.min, amount);
        this.count++;
    }
}
