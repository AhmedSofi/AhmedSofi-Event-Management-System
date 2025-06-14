package com.ems.observers;

import com.ems.models.Event;
import com.ems.models.User;
import com.ems.services.NotificationService;

/**
 * Concrete implementation of EventObserver for in-app notifications.
 */
public class InAppNotificationObserver implements EventObserver {
    private User user;
    private NotificationService notificationService;


    public InAppNotificationObserver(User user, NotificationService notificationService) {
        this.user = user;
        this.notificationService = notificationService;
    }

    @Override
    public void update(Event event, String message) {
        String notificationTitle = "Update for event: " + event.getTitle();
        notificationService.createNotification(user.getUserId(), notificationTitle, message);
    }
}
