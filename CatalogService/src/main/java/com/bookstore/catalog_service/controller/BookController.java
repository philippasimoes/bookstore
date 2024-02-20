package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.dto.BookDto;
import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/books")
public class BookController {

  @Autowired BookService bookService;

  @PostMapping
  public ResponseEntity<Book> addNewBook(@Validated @RequestBody BookDto bookDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addNewBook(bookDto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<String> updateAvailability(
      @PathVariable int id, @RequestParam Availability availability) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(bookService.updateAvailability(id, availability));
  }

  @GetMapping("/availability/{availability}")
  public ResponseEntity<List<BookDto>> getAvailableBooks(@RequestParam Availability availability) {
    List<BookDto> books = bookService.getBooksByAvailability(availability);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/genre")
  public ResponseEntity<List<BookDto>> getBooksByGenre(@RequestParam String genre) {
    List<BookDto> books = bookService.getBooksByGenre(genre);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/category")
  public ResponseEntity<List<BookDto>> getBooksByCategory(@RequestParam String category) {
    List<BookDto> books = bookService.getBooksByCategory(category);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/collection")
  public ResponseEntity<List<BookDto>> getBooksByCollection(@RequestParam String collection) {
    List<BookDto> books = bookService.getBooksByCollection(collection);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/series")
  public ResponseEntity<List<BookDto>> getBooksBySeries(@RequestParam boolean series) {
    List<BookDto> books = bookService.getBooksBySeries(series);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
  @GetMapping("/price")
  public ResponseEntity<List<BookDto>> getBooksInPriceRange(@RequestParam double startPrice, @RequestParam double endPrice) {
    List<BookDto> books = bookService.getBooksInPriceRange(startPrice, endPrice);

    if (!books.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/author/{author_id}")
  public ResponseEntity<List<BookDto>> getAllBooksFromAuthor(@RequestParam int author_id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByAuthor(author_id));
  }

  @GetMapping("/language/{language_id}")
  public ResponseEntity<List<BookDto>> getAllBooksFromLanguage(@RequestParam int language_id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByLanguage(language_id));
  }

  @GetMapping("/tag/{tag_id}")
  public ResponseEntity<List<BookDto>> getAllBooksFromTag(@RequestParam int tag_id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByTag(tag_id));
  }
}
