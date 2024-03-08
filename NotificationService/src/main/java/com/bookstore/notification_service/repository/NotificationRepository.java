package com.bookstore.notification_service.repository;

import com.bookstore.notification_service.model.entity.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Access to NotificationService database tables.
 *
 * @author Filipa Simões
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

  /**
   * Query notification table to find the notifications that have the specified book identifier and
   * customer e-mail.
   *
   * @param bookId the book identifier.
   * @param customerEmail the customer e-mail.
   * @return a Notification if exists.
   */
  Optional<Notification> findByBookIdAndCustomerEmail(int bookId, String customerEmail);
}
