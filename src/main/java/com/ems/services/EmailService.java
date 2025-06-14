package com.ems.services;

import com.ems.exceptions.EventManagementException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service for sending email notifications.
 * Uses JavaMail API to send emails via SMTP.
 */
public class EmailService {
    private final String SMTP_HOST = "smtp.gmail.com";
    private final int SMTP_PORT = 587;
    private final String SMTP_USERNAME = "am491762@gmail.com";
    private final String SMTP_PASSWORD = "wpmoshbglgveqhbf";

    /**
     * Sends an email to the specified recipient.
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param body The email body
     *          EventManagementSystem.Team@gmail.com
     *             xkprrcicoruevtyh
     */
    public void sendEmail(String toEmail, String subject, String body) {
        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Create session with authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            throw new EventManagementException("Failed to send email", e);
        }
    }
}
