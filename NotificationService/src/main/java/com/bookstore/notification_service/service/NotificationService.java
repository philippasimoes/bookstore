package com.bookstore.notification_service.service;

public interface NotificationService {
  void createNotification(int bookId, String customerEmail);

  void updateNotification(int notificationId);
}
