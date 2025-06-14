package com.ems.services;

import com.ems.config.DatabaseConnection;
import com.ems.dao.NotificationDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Notification;
import com.ems.models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing notifications in the system.
 * Handles creation, retrieval, and status updates for notifications.
 * Implements the Singleton pattern for database connection management.
 */
public class NotificationService {
    private static NotificationService instance;
    private NotificationDAO notificationDAO;
    private Connection connection;
    private EmailService emailService;

    // Private constructor for Singleton pattern
    public NotificationService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.notificationDAO = new NotificationDAO(connection);
        this.emailService = new EmailService();
    }

    /**
     * Gets the singleton instance of NotificationService.
     * @return The NotificationService instance
     */
    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /**
     * Creates a new notification in the system.
     * @param userId The ID of the user to receive the notification
     * @param title The title of the notification
     * @param message The content of the notification
     * @return The created Notification object
     */
    public Notification createNotification(int userId, String title, String message) {
        try {
            Notification notification = new Notification(0, userId, title, message, false, LocalDateTime.now());
            notification = notificationDAO.createNotification(notification);

            // Send email notification in parallel
            new Thread(() -> sendEmailNotification(userId, title, message)).start();

            return notification;
        } catch (SQLException e) {
            throw new EventManagementException("Failed to create notification", e);
        }
    }

    /**
     * Retrieves all notifications for a specific user.
     * @param userId The ID of the user
     * @return List of notifications for the user
     */
    public List<Notification> getNotificationsForUser(int userId) {
        try {
            return notificationDAO.getNotificationsByUser(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve notifications", e);
        }
    }

    /**
     * Retrieves unread notifications for a specific user.
     * @param userId The ID of the user
     * @return List of unread notifications
     */
    public List<Notification> getUnreadNotifications(int userId) {
        try {
            return notificationDAO.getUnreadNotifications(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve unread notifications", e);
        }
    }

    /**
     * Marks a notification as read.
     * @param notificationId The ID of the notification to mark as read
     */
    public void markAsRead(int notificationId) {
        try {
            notificationDAO.markAsRead(notificationId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to mark notification as read", e);
        }
    }

    /**
     * Marks all notifications for a user as read.
     * @param userId The ID of the user
     */
    public void markAllAsRead(int userId) {
        try {
            notificationDAO.markAllAsRead(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to mark notifications as read", e);
        }
    }

    /**
     * Sends an email notification in addition to the in-app notification.
     * @param userId The ID of the user to notify
     * @param title The title of the notification
     * @param message The content of the notification
     */
    private void sendEmailNotification(int userId, String title, String message) {
        try {
            UserService userService = new UserService();
            User user = userService.getUserById(userId);

            if (user != null) {
                String emailBody = "Dear " + user.getFirstName() + ",\n\n" +
                        "You have a new notification:\n\n" +
                        title + "\n\n" +
                        message + "\n\n" +
                        "Thank you,\n" +
                        "Event Management System";

                emailService.sendEmail(user.getEmail(), "Notification: " + title, emailBody);
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            // Fail silently for email notifications as they are secondary
        }
    }

    /**
     * Sends a batch of notifications for system-wide announcements.
     * @param userIds List of user IDs to receive the notification
     * @param title The title of the notification
     * @param message The content of the notification
     */
    public void sendBatchNotification(List<Integer> userIds, String title, String message) {
        try {
            connection.setAutoCommit(false);

            for (int userId : userIds) {
                createNotification(userId, title, message);
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new EventManagementException("Failed to rollback batch notification", ex);
            }
            throw new EventManagementException("Failed to send batch notifications", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new EventManagementException("Failed to reset auto-commit", e);
            }
        }
    }

    /**
     * Deletes a notification from the system.
     * @param notificationId The ID of the notification to delete
     */
    public void deleteNotification(int notificationId) {
        try {
            notificationDAO.deleteNotification(notificationId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to delete notification", e);
        }
    }

    /**
     * Gets the count of unread notifications for a user.
     * @param userId The ID of the user
     * @return The count of unread notifications
     */
    public int getUnreadCount(int userId) {
        try {
            return notificationDAO.getUnreadCount(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to get unread notification count", e);
        }
    }
}
