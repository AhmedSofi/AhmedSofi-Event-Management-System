package com.ems.observers;

import com.ems.models.Event;
import com.ems.models.User;
import com.ems.services.EmailService;

/**
 * Concrete implementation of EventObserver for email notifications.
 */
public class EmailNotificationObserver implements EventObserver {
    private User user;
    private EmailService emailService;

    public EmailNotificationObserver(User user, EmailService emailService) {
        this.user = user;
        this.emailService = emailService;
    }

    @Override
    public void update(Event event, String message) {
        String subject = "Update for event: " + event.getTitle();
        String body = "Dear " + user.getFirstName() + ",\n\n" +
                "There's an update for the event you're registered for:\n\n" +
                message + "\n\n" +
                "Event details:\n" +
                "Title: " + event.getTitle() + "\n" +
                "Date: " + event.getStartDateTime() + "\n" +
                "Venue: " + (event.getVenue() != null ? event.getVenue().getName() : "TBD") + "\n\n" +
                "Thank you,\n" +
                "Event Management System";

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
