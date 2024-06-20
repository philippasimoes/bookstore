package com.bookstore.notification_service.service;

import com.bookstore.notification_service.exception.NotificationNotFoundException;
import com.bookstore.notification_service.model.dto.enums.NotificationType;
import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.repository.NotificationRepository;
import com.bookstore.notification_service.utils.NotificationUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Notification Service class.
 *
 * @author Filipa Simões
 */
@Service
public class StockNotificationService {

    private static final Logger LOGGER = LogManager.getLogger(StockNotificationService.class);
    private static final String STOCK_URL = "http://stock-service:10001/stock/";

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CircuitBreakerFactory circuitBreakerFactory;

    /**
     * Create a notification when requested by the user.
     *
     * @param bookId        the book identifier.
     * @param customerEmail the user email.
     */
    public void createNotification(int bookId, String customerEmail) {

        if (notificationRepository.findByBookIdAndCustomerEmail(bookId, customerEmail).isEmpty()) {
            Notification notification = new Notification();
            notification.setBookId(bookId);
            notification.setCustomerEmail(customerEmail);
            notification.setNotificationType(NotificationType.BOOK_AVAILABILITY);
            notification.setSent(false);
            notificationRepository.save(notification);
        } else {
            throw new DuplicateKeyException(
                    "A notification with this book id and customer email already exists");
        }
    }

    /**
     * Update the notification and notify the user that the book is available.
     *
     * @param notificationId the notification identifier.
     */
    public void updateNotification(int notificationId) {

        Optional<Notification> notification = notificationRepository.findById(notificationId);

        if (notification.isPresent()) {
            if (verifyBookStock(notification.get().getBookId()) > 0) {

                Notification foundNotification = notification.get();
                String message =
                        "The book with id" + notification.get().getBookId() + " is available for purchase.";
                foundNotification.setMessage(message);
                foundNotification.setSent(true);
                Notification notificationUpdated = notificationRepository.save(foundNotification);

                NotificationUtils.sendMail(
                        notificationUpdated.getCustomerEmail(),
                        "Book Available for Purchase",
                        notificationUpdated.getMessage());
            }
        } else throw new NotificationNotFoundException();
    }

    /**
     * Check which notifications have not yet been sent and update them.
     */
    @Job(name = "Verify unsent notifications")
    public void verifyUnsentNotifications() {

        List<Notification> notifications = notificationRepository.findAll();

        List<Notification> filteredNotifications =
                notifications.stream()
                        .filter(
                                notification ->
                                        notification.getNotificationType().equals(NotificationType.BOOK_AVAILABILITY))
                        .filter(notification -> !notification.isSent())
                        .toList();

        for (Notification notification : filteredNotifications) {
            updateNotification(notification.getId());
        }
    }

    /**
     * Validates if the book available stock is above zero.
     *
     * @param bookId the book identifier.
     * @return the units available.
     */
    private Integer verifyBookStock(int bookId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

        return circuitBreaker.run(
                () ->
                        restTemplate
                                .exchange(STOCK_URL + bookId, HttpMethod.GET, null, Integer.class)
                                .getBody(),
                throwable -> {
                    LOGGER.log(Level.WARN, "Error connecting to stock service.", throwable);
                    return 0;
                });
    }
}
