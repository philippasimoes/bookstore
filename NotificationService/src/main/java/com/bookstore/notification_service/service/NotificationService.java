package com.bookstore.notification_service.service;

import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.repository.NotificationRepository;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Notification Service class.
 *
 * @author Filipa Simões
 */
@Service
public class NotificationService {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(NotificationService.class);


    /**
     * NotificationRepository injection to access the database.
     */
    @Autowired
    NotificationRepository notificationRepository;

    /**
     * JavaMailSender injection.
     */
    @Autowired
    JavaMailSender mailSender;


    /**
     * RestTemplate to communicate with other services.
     */
    RestTemplate restTemplate = new RestTemplate();


    /**
     * Create a notification when requested by the user.
     *
     * @param bookId        the book identifier.
     * @param customerEmail the user email.
     * @return a new notification.
     */
    public Notification createNotification(int bookId, String customerEmail) {

        if (!notificationRepository.findByBookIdAndCustomerEmail(bookId, customerEmail).isPresent()) {
            Notification notification = new Notification();
            notification.setBookId(bookId);
            notification.setCustomerEmail(customerEmail);
            notification.setSent(false);
            return notificationRepository.save(notification);
        } else {
            throw new RuntimeException("A notification with this book id and customer email already exists");
        }
    }

    /**
     * Update the notification and notify the user that the book is available.
     *
     * @param bookId the book identifier.
     * @param notificationId  the notification identifier.
     */
    public void updateNotification(int bookId, int notificationId) {


        if (verifyBookStock(bookId)) {
            String message = "The book with id" + bookId + " is available for purchase.";
            Notification notification = notificationRepository.findById(notificationId).get();
            notification.setMessage(message);
            notification.setSent(true);
            Notification notificationUpdated = notificationRepository.save(notification);

            sendEmail(notificationUpdated.getCustomerEmail(), "Book Available for Purchase", notificationUpdated.getMessage());

        }
    }


    /**
     * Check which notifications have not yet been sent and update them.
     */
    @Job(name = "Verify unsent notifications")
    public void verifyUnsentNotifications() {

        List<Notification> notifications = notificationRepository.findAll();

        List<Notification> filteredNotifications = notifications.stream()
                .filter(notification -> !notification.isSent())
                .collect(Collectors.toList());

        for (Notification notification : filteredNotifications) {
            updateNotification(notification.getBookId(), notification.getId());

        }
    }


    /**
     * Send an e-mail.
     *
     * @param to      the e-mail receiver.
     * @param subject the e-mail subject.
     * @param body    the e-mail body.
     */
    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        LOGGER.info(String.format("Book available, email sent to %s", to));
    }

    /**
     * Validates if the book available stock is above zero.
     *
     * @param bookId the book identifier.
     * @return true if the stock is above zero.
     */
    public boolean verifyBookStock(int bookId) {

        return Boolean.TRUE.equals(restTemplate.getForObject("http://stock-service:10000/stock/" + bookId, Boolean.class));
    }

}
