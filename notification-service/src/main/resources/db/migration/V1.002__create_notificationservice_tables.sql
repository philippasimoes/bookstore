CREATE TABLE notificationservice.notification (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   book_id INTEGER,
   order_id INTEGER,
   customer_email VARCHAR(255),
   creation_date TIMESTAMP WITHOUT TIME ZONE,
   update_date TIMESTAMP WITHOUT TIME ZONE,
   message VARCHAR(255),
   sent BOOLEAN,
   notification_type VARCHAR(255),
   CONSTRAINT pk_notification PRIMARY KEY (id)
);