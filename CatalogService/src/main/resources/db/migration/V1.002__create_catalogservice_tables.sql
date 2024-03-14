CREATE TABLE catalogservice.book (
    id INT2 NOT NULL PRIMARY KEY,
    title VARCHAR(512),
    original_title VARCHAR(512),
    isbn   VARCHAR(20),
    format VARCHAR(20),
    release_date VARCHAR(10),
    edition_date VARCHAR(10),
    genre VARCHAR(50),
    edition VARCHAR(50),
    series BIT,
    publisher VARCHAR(50),
    synopsis VARCHAR(1024),
    price NUMERIC,
    promotional_price NUMERIC,
    category VARCHAR(50),
    collection VARCHAR(50),
    availability VARCHAR(50),
    creation_date TIMESTAMP,
    update_date TIMESTAMP,
    stock_available INTEGER
  );

CREATE TABLE catalogservice.author (
    id INT2 NOT NULL PRIMARY KEY,
    isni VARCHAR(512),
    name VARCHAR(512),
    original_full_name VARCHAR(512),
    date_of_birth VARCHAR(10),
    place_of_birth VARCHAR(256),
    date_of_death VARCHAR(10),
    place_of_death VARCHAR(256),
    about VARCHAR(1024)
  );

CREATE TABLE catalogservice.book_sample (
    id INT2 NOT NULL PRIMARY KEY,
    book_id INT2 NOT NULL,
    sample VARCHAR(1024)
  );

CREATE TABLE catalogservice.language (
    id INT2 NOT NULL PRIMARY KEY,
    code VARCHAR(10) NOT NULL
  );

CREATE TABLE catalogservice.bookTag (
    id INT2 NOT NULL PRIMARY KEY,
    value VARCHAR(50) NOT NULL
    );