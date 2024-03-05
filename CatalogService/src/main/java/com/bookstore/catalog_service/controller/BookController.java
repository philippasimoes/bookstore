package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.getAllBooks();

        if (!books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(books);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookByID(id));
    }

    @GetMapping("/availability")
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

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookDto>> getAllBooksFromAuthor(@RequestParam int authorId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByAuthor(authorId));
    }

    @GetMapping("/language/{languageId}")
    public ResponseEntity<List<BookDto>> getAllBooksFromLanguage(@RequestParam int languageId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByLanguage(languageId));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<BookDto>> getAllBooksFromTag(@RequestParam int tagId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByTag(tagId));
    }

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

    @PatchMapping("/sample/{bookId}")
    public ResponseEntity<String> addBookSample(@PathVariable int bookId, @RequestBody String sample) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.addBookSample(bookId, sample));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.bookExistsById(id));
    }
}
