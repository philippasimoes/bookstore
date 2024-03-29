CREATE TABLE userservice."user" (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name VARCHAR(255),
   email VARCHAR(255),
   username VARCHAR(255),
   password VARCHAR(255),
   address VARCHAR(255),
   postal_code VARCHAR(255),
   role VARCHAR(255),
   enabled BOOLEAN,
   creation_date TIMESTAMP WITHOUT TIME ZONE,
   update_date TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT pk_user PRIMARY KEY (id)
);