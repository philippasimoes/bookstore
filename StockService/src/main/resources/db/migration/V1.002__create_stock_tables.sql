CREATE TABLE stockservice.stock (
     id              INT2 NOT NULL PRIMARY KEY,
     book_id         INT2 NOT NULL,
     units INT2 NOT NULL,
     creation_date   TIMESTAMP,
     update_date     TIMESTAMP
  );