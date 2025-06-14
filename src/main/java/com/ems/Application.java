package com.ems;

import com.ems.services.EmailService;

public class Application {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        emailService.sendEmail("EventManagementSystem.Team@gmail.com","© 2025 Event Management System","Test The Email Service \n" +"\n"+
                "Kind regards,\n" +
                "Event Management System Team\n");
    }
}