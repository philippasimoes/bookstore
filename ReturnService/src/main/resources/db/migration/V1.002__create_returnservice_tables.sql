CREATE TABLE returnservice.return (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   order_id INTEGER,
   item_id_list INTEGER[],
   return_reason VARCHAR(255),
   date TIMESTAMP WITHOUT TIME ZONE,
   return_status VARCHAR(255),
   CONSTRAINT pk_return PRIMARY KEY (id)
);