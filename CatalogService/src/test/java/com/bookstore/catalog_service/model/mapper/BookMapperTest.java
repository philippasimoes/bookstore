package com.bookstore.catalog_service.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.PublisherDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.dto.enums.Format;
import com.bookstore.catalog_service.model.entity.Book;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import com.bookstore.catalog_service.model.entity.Publisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {BookMapperImpl.class})
@ActiveProfiles(value = "test")
public class BookMapperTest {
  @Autowired BookMapper bookMapper;

  Publisher publisher =
      new Publisher("publisher-name", "publisher@publisher.com", "123456789");

  PublisherDto publisherDto =
          new PublisherDto(1,"publisher-name", "publisher@publisher.com", "123456789");
  Book book =
      new Book(
          "title",
          "original_title",
          "isbn_1",
          "2024-02-19 10:22:40.379",
          "2024-02-19 10:22:40.379",
          "genre_1",
          "edition_1",
          true,
          publisher,
          "synopsis_1",
          10.50,
          10.00,
          "collection_1",
          "category_1",
          Availability.AVAILABLE,
          Format.DIGITAL,
          new HashSet<>(),
          new HashSet<>(),
          new HashSet<>(),
          0,
          0.0);

  BookDto bookDto =
      new BookDto(
          2,
          "title_2",
          "original_title_2",
          "isbn_2",
          "2024-02-19 10:22:40.379",
          "2024-02-19 10:22:40.379",
          "genre_2",
          "edition_2",
          false,
          publisherDto,
          "synopsis_2",
          12.50,
          10.00,
          "collection_2",
          "category_2",
          Availability.AVAILABLE,
          Format.HARDCOVER,
          new HashSet<>(),
          new HashSet<>(),
          new HashSet<>(),
          0,
          0.0);

  @Test
  public void testBookModelToBookDto() {

    // Arrange && Act
    BookDto bookDto = bookMapper.toDto(book);

    // Assert
    assertEquals(book.getId(), bookDto.getId());
    assertEquals(book.getIsbn(), bookDto.getIsbn());
    assertEquals(book.getTitle(), bookDto.getTitle());
    assertEquals(book.getPrice(), bookDto.getPrice());
  }

  @Test
  public void testBookDtoToBook() {

    // Arrange && Act
    Book book = bookMapper.toEntity(bookDto);

    // Assert
    assertEquals(bookDto.getId(), book.getId());
    assertEquals(bookDto.getAvailability(), book.getAvailability());
    assertEquals(bookDto.getReleaseDate(), book.getReleaseDate());
    assertEquals(bookDto.getPromotionalPrice(), book.getPromotionalPrice());
  }
}
