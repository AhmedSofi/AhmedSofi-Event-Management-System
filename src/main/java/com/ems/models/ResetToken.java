package com.ems.models;

public class ResetToken {
    private String token;
    private int userId;
    private long expirationTime;
    private int attempts; // عدد المحاولات

    public ResetToken(String token, int userId, long expirationTime) {
        this.token = token;
        this.userId = userId;
        this.expirationTime = expirationTime;
        this.attempts = 0;
    }

    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public long getExpirationTime() { return expirationTime; }
    public int getAttempts() { return attempts; }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public boolean isBlocked() {
        return this.attempts >= 3;
    }
}
