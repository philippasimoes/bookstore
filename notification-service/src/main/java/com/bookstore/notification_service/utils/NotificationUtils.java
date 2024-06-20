package com.bookstore.notification_service.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class NotificationUtils {

    private static final Logger LOGGER = LogManager.getLogger(NotificationUtils.class);

    public NotificationUtils() {
    }

    public static void sendMail(String to, String subject, String body) {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        LOGGER.log(Level.INFO, "Email sent to {}", to);

    }
}
