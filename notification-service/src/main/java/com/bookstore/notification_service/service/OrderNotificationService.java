package com.bookstore.notification_service.service;

import com.bookstore.notification_service.model.dto.OrderDto;
import com.bookstore.notification_service.model.dto.enums.NotificationType;
import com.bookstore.notification_service.model.entity.Notification;
import com.bookstore.notification_service.repository.NotificationRepository;
import com.netflix.discovery.converters.Auto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

  private static final Logger LOGGER = LogManager.getLogger(OrderNotificationService.class);

  /** NotificationRepository injection to access the database. */
  @Autowired NotificationRepository notificationRepository;

  /** JavaMailSender injection. */
  @Autowired JavaMailSender mailSender;

  public void createNotification(OrderDto order, String customerEmail, String trackingNumber) {
    if (notificationRepository
        .findByOrderIdAndCustomerEmail(order.getId(), customerEmail)
        .isEmpty()) {
      Notification notification = new Notification();
      notification.setOrderId(order.getId());
      notification.setCustomerEmail(customerEmail);
      notification.setNotificationType(NotificationType.ORDER_SHIPPED);
      notification.setSent(true);
      notificationRepository.save(notification);

      String emailBody =
          String.format(
              """
                          You're order with id %s was shipped with tracking number %s. It should be received in the next days.Here is a resume of your order:\s
                           Items: %s\s
                           Items price: %s\s
                           Tax price: %s\s
                           Total price: %s.""",
              order.getId(),
              trackingNumber,
              order.getItems(),
              order.getTotalPriceItems(),
              order.getTax(),
              order.getTotalPriceOrder());

      sendEmail(customerEmail, "Your order from Bookstore was shipped", emailBody);
    } else {
      throw new DuplicateKeyException(
          "A notification with this order id and customer email already exists");
    }
  }

  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
    LOGGER.log(Level.INFO, "Order shipped, email sent to {}", to);
  }
}
