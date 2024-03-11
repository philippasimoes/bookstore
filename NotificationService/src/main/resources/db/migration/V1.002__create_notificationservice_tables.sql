CREATE TABLE notificationservice.notification (
    id INT2 NOT NULL PRIMARY KEY,
    book_id INT2,
    customer_email VARCHAR(50),
    creation_date TIMESTAMP,
    notification_date TIMESTAMP,
    sent BIT
  );