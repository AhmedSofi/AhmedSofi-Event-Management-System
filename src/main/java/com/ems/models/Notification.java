package com.ems.models;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a notification sent to a user in the system.
 */
public class Notification {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Date createdDateUtil;

    public Notification(int notificationId, int userId, String title,
                        String message, boolean isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Date getCreatedDateUtil() {
        return createdDateUtil;
    }

    public void setCreatedDateUtil(Date createdDateUtil) {
        this.createdDateUtil = createdDateUtil;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
