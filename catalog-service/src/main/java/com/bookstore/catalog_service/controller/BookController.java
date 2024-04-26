package com.bookstore.catalog_service.controller;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Book Service.
 *
 * @author Filipa Simões
 */
@RestController
@RequestMapping(value = "/books")
@Tag(name = "Book endpoints")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {

    this.bookService = bookService;
  }

  @Operation(summary = "Get all books.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = BookDto.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No books in database.")
      })
  @GetMapping
  public ResponseEntity<List<BookDto>> getAllBooks() {
    List<BookDto> books = bookService.getAllBooks();

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get a book by identifier.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Book found.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BookDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Book not found.")
      })
  @GetMapping("/{id}")
  public ResponseEntity<BookDto> getBookById(@PathVariable int id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookByID(id));
  }

  @Operation(summary = "Get all available books.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of available books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No available books in database.")
      })
  @GetMapping("/availability")
  public ResponseEntity<List<BookDto>> getAvailableBooks(@RequestParam Availability availability) {
    List<BookDto> books = bookService.getBooksByAvailability(availability);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by genre.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "No books with the specified genre in database.")
      })
  @GetMapping("/genre")
  public ResponseEntity<List<BookDto>> getBooksByGenre(@RequestParam String genre) {
    List<BookDto> books = bookService.getBooksByGenre(genre);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by category.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "No books with the specified category in database.")
      })
  @GetMapping("/category")
  public ResponseEntity<List<BookDto>> getBooksByCategory(@RequestParam String category) {
    List<BookDto> books = bookService.getBooksByCategory(category);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by collection.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "No books with the specified collection in database.")
      })
  @GetMapping("/collection")
  public ResponseEntity<List<BookDto>> getBooksByCollection(@RequestParam String collection) {
    List<BookDto> books = bookService.getBooksByCollection(collection);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get all books that belong to a series.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "The database does not contain books that belong to a series.")
      })
  @GetMapping("/series")
  public ResponseEntity<List<BookDto>> getBooksBySeries(@RequestParam boolean series) {
    List<BookDto> books = bookService.getBooksBySeries(series);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Returns books that are in a price range.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "There are no books within the selected price range.")
      })
  @GetMapping("/price")
  public ResponseEntity<List<BookDto>> getBooksInPriceRange(
      @RequestParam double startPrice, @RequestParam double endPrice) {
    List<BookDto> books = bookService.getBooksInPriceRange(startPrice, endPrice);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by author.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No books by this author in database.")
      })
  @GetMapping("/author")
  public ResponseEntity<List<BookDto>> getAllBooksFromAuthor(@RequestParam int authorId) {
    List<BookDto> books = bookService.getBooksByAuthor(authorId);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by language.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No books with this language in database.")
      })
  @GetMapping("/language")
  public ResponseEntity<List<BookDto>> getAllBooksFromLanguage(@RequestParam int languageId) {

    List<BookDto> books = bookService.getBooksByLanguage(languageId);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by tag.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(responseCode = "204", description = "No books with this tag in database.")
      })
  @GetMapping("/tag")
  public ResponseEntity<List<BookDto>> getAllBooksFromTag(@RequestParam int tagId) {

    List<BookDto> books = bookService.getBooksByTag(tagId);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get books by publisher.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show a list of books.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Book.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "No books with this publisher in database.")
      })
  @GetMapping("/publisher")
  public ResponseEntity<List<BookDto>> getAllBooksFromPublisher(@RequestParam int publisherId) {
    List<BookDto> books = bookService.getBooksByPublisher(publisherId);

    if (!books.isEmpty()) {
      return ResponseEntity.ok(books);
    } else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @Operation(summary = "Add a book to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Book added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(
            responseCode = "302",
            description = "Book with this ISBN already exists in database."),
        @ApiResponse(responseCode = "401", description = "The user is not authenticated."),
        @ApiResponse(
            responseCode = "403",
            description = "The user doesn't have permission to create books.")
      })
  @SecurityRequirement(name = "admin-only")
  @PostMapping
  public ResponseEntity<BookDto> addNewBook(@Validated @RequestBody BookDto bookDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addNewBook(bookDto));
  }

  @Operation(summary = "Update an existing book.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Book updated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "401", description = "The user is not authenticated."),
        @ApiResponse(
            responseCode = "403",
            description = "The user is not authorized to update the book.")
      })
  @SecurityRequirement(name = "admin-only")
  @PutMapping("/update")
  public ResponseEntity<Book> updateBook(@RequestBody BookDto bookDto) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(bookDto));
  }

  @Operation(summary = "Update the availability of an existing book.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Book updated.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "401", description = "The user is not authenticated."),
        @ApiResponse(
            responseCode = "403",
            description = "The user is not authorized to update the book.")
      })
  @SecurityRequirement(name = "admin-only")
  @PatchMapping("/{id}")
  public ResponseEntity<String> updateAvailability(
      @PathVariable int id, @RequestParam Availability availability) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(bookService.updateAvailability(id, availability));
  }

  @Operation(summary = "Add an book sample to database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Book sample added.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "401", description = "The user is not authenticated."),
        @ApiResponse(
            responseCode = "403",
            description = "The user doesn't have permission to create book samples.")
      })
  @SecurityRequirement(name = "admin-only")
  @PatchMapping("/sample/{bookId}")
  public ResponseEntity<String> addBookSample(
      @PathVariable int bookId, @RequestBody String sample) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.addBookSample(bookId, sample));
  }

  @Operation(summary = "Validates if the book is present in database.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Book exists.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthorDto.class))
            }),
        @ApiResponse(responseCode = "401", description = "The user is not authenticated."),
        @ApiResponse(
            responseCode = "403",
            description = "The user is not authorized to update the author.")
      })
  @SecurityRequirement(name = "admin-only")
  @GetMapping("/confirmation/{id}")
  public ResponseEntity<Integer> existsById(@PathVariable int id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookByID(id).getId());
  }

  @GetMapping("/report")
  public void generateReport(HttpServletResponse response) throws IOException, JRException {
    bookService.exportReport(response);
  }
}
