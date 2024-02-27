package com.bookstore.notification_service.service;

import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;


/**
 * Notification Service class.
 *
 * @author Filipa Simões
 */
@Service
public class NotificationService {

    private static final Logger LOGGER = new JobRunrDashboardLogger(LoggerFactory.getLogger(NotificationService.class));

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    JavaMailSender mailSender;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    ObjectMapper objectMapper;

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
     * Update the existent notification - used when the book is available and the user needs to be notified.
     *
     * @param argument a json string containing a pair of book identifier and notification identifier.
     */
    public void updateNotification(String argument) {

        try {
            Pair<Integer, Integer> pair = objectMapper.readValue(argument, Pair.class);

            if (verifyBookStock(pair.getFirst())) {
                String message = "The book with id" + pair.getFirst() + " is available for purchase.";
                Notification notification = notificationRepository.findById(pair.getSecond()).get();
                notification.setMessage(message);
                notification.setSent(true);
                Notification notificationUpdated = notificationRepository.save(notification);

                sendEmail(notificationUpdated.getCustomerEmail(), "Book Available for Purchase", notificationUpdated.getMessage());

            } else LOGGER.info("Book is not available");
        } catch (JsonProcessingException e) {
            LOGGER.error("Error reading the method argument: " + e.getMessage());
        }

    }


    /**
     * Get all notifications from database.
     *
     * @return a list of Notification.
     */
    public List<Notification> findByAll() {

        return notificationRepository.findAll();
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
    public boolean verifyBookStock(@PathVariable int bookId) {

        return Boolean.TRUE.equals(restTemplate.getForObject("http://stock-service:10000/stock/" + bookId, Boolean.class));
    }

}
