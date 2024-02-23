package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.dto.enums.Format;
import com.bookstore.catalog_service.model.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BookMapperImpl.class})
public class BookMapperTest {
  @Autowired
  BookMapper bookMapper;


  //jfixture, add mvn dependency (generate mocked objects)
  Book book =
      new Book(
          1,
          "title",
          "original_title",
          "isbn_1",
          "2024-02-19 10:22:40.379",
          "2024-02-19 10:22:40.379",
          "genre_1",
          "edition_1",
          true,
          "publisher_1",
          "synopsis_1",
          10.50,
          10.00,
          new Timestamp(System.currentTimeMillis()),
          null,
          "collection_1",
          "category_1",
          Availability.AVAILABLE,
          Format.DIGITAL,
          new HashSet<>(),
          new HashSet<>(),
          new HashSet<>(),
          0);

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
          "publisher_2",
          "synopsis_2",
          12.50,
          10.00,
          new Timestamp(System.currentTimeMillis()),
          null,
          "collection_2",
          "category_2",
          Availability.AVAILABLE,
          Format.HARDCOVER,
          new HashSet<>(),
          new HashSet<>(),
          new HashSet<>(),
          0);

  @Test
  public void testBookModelToBookDto() {

    //Arrange && Act
    BookDto bookDto = bookMapper.bookToBookDto(book);

    //Assert
    assertEquals(book.getId(), bookDto.getId());
    assertEquals(book.getIsbn(), bookDto.getIsbn());
    assertEquals(book.getTitle(), bookDto.getTitle());
    assertEquals(book.getPrice(), bookDto.getPrice());
  }

  @Test
  public void testBookDtoToBook() {

    //Arrange && Act
    Book book = bookMapper.bookDtoToBook(bookDto);

    //Assert
    assertEquals(bookDto.getId(), book.getId());
    assertEquals(bookDto.getAvailability(), book.getAvailability());
    assertEquals(bookDto.getReleaseDate(), book.getReleaseDate());
    assertEquals(bookDto.getPromotionalPrice(), book.getPromotionalPrice());
  }
}
