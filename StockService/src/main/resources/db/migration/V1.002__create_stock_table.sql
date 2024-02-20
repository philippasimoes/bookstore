CREATE TABLE stockservice.stock
  (
     id              INT2 NOT NULL,
     book_id         INT2 NOT NULL,
     available_stock INT2 NOT NULL,
     creation_date   TIMESTAMP,
     update_date     TIMESTAMP
     PRIMARY KEY (id)
  );