package com.bookstore.notification_service.repository;

import com.bookstore.notification_service.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Optional<Notification> findByBookIdAndCustomerEmail(int bookId, String customerEmail);

}
