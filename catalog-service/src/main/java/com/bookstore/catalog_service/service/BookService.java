package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.InputNotAcceptedException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.entity.Publisher;
import com.bookstore.catalog_service.model.mapper.AuthorMapper;
import com.bookstore.catalog_service.model.mapper.BookMapper;
import com.bookstore.catalog_service.model.mapper.BookTagMapper;
import com.bookstore.catalog_service.model.mapper.LanguageMapper;
import com.bookstore.catalog_service.repository.BookRepository;
import com.bookstore.catalog_service.repository.PublisherRepository;
import com.bookstore.catalog_service.specifications.BookSpecifications;
import com.bookstore.catalog_service.utils.BookServiceUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

/**
 * Book service class.
 *
 * @author Filipa Simões
 */
@Service
@Transactional
public class BookService {

  private static final Logger LOGGER = LogManager.getLogger(BookService.class);
  private static final String BOOK_NOT_FOUND_MESSAGE = "Book not found.";

  @Autowired ApplicationContext context;
  @Autowired BookRepository bookRepository;
  @Autowired PublisherRepository publisherRepository;
  @Autowired BookTagMapper bookTagMapper;
  @Autowired AuthorMapper authorMapper;
  @Autowired LanguageMapper languageMapper;
  @Autowired BookMapper bookMapper;
  @Autowired AuthorService authorService;
  @Autowired BookSampleService bookSampleService;
  @Autowired PublisherService publisherService;
  @Autowired LanguageService languageService;
  @Autowired BookTagService bookTagService;

  /**
   * Get all books.
   *
   * @return a list of BookDto.
   */
  public List<BookDto> getAllBooks() {

    List<BookDto> bookDtoList = new ArrayList<>();

    for (Book book : bookRepository.findAll()) {
      BookDto bookDto = bookMapper.toDto(book);
      bookDto.setBookTags(bookTagMapper.toDtoSet(book.getBookTags()));
      bookDto.setAuthors(authorMapper.toDtoSet(book.getAuthors()));
      bookDto.setLanguages(languageMapper.toDtoSet(book.getLanguages()));

      bookDtoList.add(bookDto);
    }
    return bookDtoList;
  }

  /**
   * Retrieve a list of books where availability is "Available".
   *
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByAvailability(Availability availability) {

    return bookMapper.toDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasAvailability(availability))));
  }

  /**
   * Retrieve a list of books that have the same author.
   *
   * @param authorId the author identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByAuthor(int authorId) {
    return bookMapper.toDtoList(
        bookRepository.findAll(
            Specification.where(
                BookSpecifications.allBooksFromAuthor(authorService.getAuthorById(authorId)))));
  }

  /**
   * Retrieve a list of books that have the same language.
   *
   * @param languageId the language identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByLanguage(int languageId) {
    return bookMapper.toDtoList(
        bookRepository.findAll(
            Specification.where(
                BookSpecifications.allBooksFromLanguage(
                    languageService.getLanguageById(languageId)))));
  }

  /**
   * Retrieve a list of books that have the same tag.
   *
   * @param bookTagId the tag identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByTag(int bookTagId) {

    BookTag bookTag = bookTagService.getTagById(bookTagId);

    return bookMapper.toDtoList(
        bookRepository.findAll(Specification.where(BookSpecifications.allBooksFromTag(bookTag))));
  }

  /**
   * Retrieve a list of books that have the same publisher.
   *
   * @param publisherId the publisher identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByPublisher(int publisherId) {
    Optional<Publisher> publisher = publisherRepository.findById(publisherId);

    if (publisher.isPresent()) {
      return bookMapper.toDtoList(
          bookRepository.findAll(
              Specification.where(BookSpecifications.allBooksFromPublisher(publisher.get()))));
    } else throw new ResourceNotFoundException("Publisher not found");
  }

  /**
   * Retrieve a list of books that have the same genre.
   *
   * @param genre the book genre.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByGenre(String genre) {

    return bookMapper.toDtoList(
        bookRepository.findAll(Specification.where(BookSpecifications.hasValue("genre", genre))));
  }

  /**
   * Retrieve a list of books that have the same category.
   *
   * @param category the book category.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByCategory(String category) {

    return bookMapper.toDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasValue("category", category))));
  }

  /**
   * Retrieve a list of books that have the same collection.
   *
   * @param collection the book collection.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByCollection(String collection) {

    return bookMapper.toDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasValue("collection", collection))));
  }

  /**
   * Retrieve a list of books that belongs to a series or not.
   *
   * @param isInASeries the boolean flag used.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksBySeries(boolean isInASeries) {

    return bookMapper.toDtoList(bookRepository.findBookBySeries(isInASeries));
  }

  /**
   * Retrieve a list of books that are in a determined price range.
   *
   * @param startPrice the range lower limit.
   * @param endPrice the range upper limit.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksInPriceRange(double startPrice, double endPrice) {

    if (endPrice > startPrice) {
      return bookMapper.toDtoList(bookRepository.findByPriceBetween(startPrice, endPrice));
    } else
      throw new InputNotAcceptedException("The start price should bw lower than the end price");
  }

  /**
   * Get book from database using the unique identifier.
   *
   * @param id the unique identifier.
   * @return the BookDto object.
   */
  public BookDto getBookById(int id) {

    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE));

    return bookMapper.toDto(book);
  }

  /**
   * Add new book to the database.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return the persisted book (entity).
   */
  public BookDto addNewBook(BookDto bookDto) {

    Optional<Book> book = bookRepository.findByIsbn(bookDto.getIsbn());

    if (book.isPresent()) {
      throw new DuplicatedResourceException(
          "Book with ISBN " + bookDto.getIsbn() + "already exists.");
    } else {
      // get other needed entities from book dto
      Publisher publisher = publisherService.getPublisherFromBook(bookDto);
      Set<Language> languageSet = languageService.getLanguageFromBook(bookDto);
      Set<BookTag> bookTagSet = bookTagService.getTagsFromBook(bookDto);
      Set<Author> authorSet = authorService.getAuthorsFromBook(bookDto);

      Book newBook = bookMapper.toEntity(bookDto);
      newBook.setPublisher(publisher);
      newBook.setLanguages(languageSet);
      newBook.setBookTags(bookTagSet);
      newBook.setAuthors(authorSet);

      Book bookWithLists = bookRepository.save(newBook);

      // create stock entry for this book - the entry is created with 0 units.
      BookServiceUtils.createStock(bookWithLists.getId());

      return bookMapper.toDto(bookWithLists);
    }
  }

  /**
   * Update an existent book.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return The updated book (entity).
   */
  public Book updateBook(BookDto bookDto) {
    Optional<Book> book = bookRepository.findById(bookDto.getId());

    if (book.isPresent()) {

      Book updatedBook = bookRepository.save(bookMapper.toEntity(bookDto));

      if (!languageService.getLanguageFromBook(bookDto).isEmpty()) {
        updatedBook.setLanguages(languageService.getLanguageFromBook(bookDto));
      }

      if (!bookTagService.getTagsFromBook(bookDto).isEmpty()) {
        updatedBook.setBookTags(bookTagService.getTagsFromBook(bookDto));
      }

      if (!authorService.getAuthorsFromBook(bookDto).isEmpty()) {
        updatedBook.setAuthors(authorService.getAuthorsFromBook(bookDto));
      }

      return bookRepository.save(updatedBook);

    } else {
      throw new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
    }
  }

  /**
   * Add a new book sample to the book.
   *
   * @param bookId the book identifier.
   * @param sample the sample content.
   * @return A message indicating that the book sample was added to the book.
   */
  public String addBookSample(int bookId, String sample) {

    Optional<Book> book = bookRepository.findById(bookId);

    if (book.isPresent()) {
      bookSampleService.addBookSample(book.get(), sample);
      return "Book sample added to book with ID" + bookId;
    } else throw new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
  }

  /**
   * Update book availability.
   *
   * @param id the book identifier.
   * @param availability the desired availability.
   * @return A message indicating the operation success or error.
   */
  public String updateAvailability(Integer id, Availability availability) {

    if (id != null && availability != null && bookRepository.findById(id).isPresent()) {
      bookRepository.updateBookAvailability(id, availability);
      return "The availability of the book with id " + id + " has been updated.";
    } else throw new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
  }

  /**
   * Consume messages in updated queue.
   *
   * @param message the message from StockService.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.updated.name}")
  public void consumeUpdatedEvents(String message) {
    updateBookFromMessage(message, Availability.AVAILABLE);
  }

  /**
   * Consume messages in sold out queue.
   *
   * @param message the message from StockService.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.soldout.name}")
  public void consumeSoldOutEvents(String message) {
    updateBookFromMessage(message, Availability.SOLD_OUT);
  }

  public void exportReport(HttpServletResponse response) {

    List<Book> books = bookRepository.findAll();

    Resource resource = context.getResource("classpath:books.jrxml");

    BookServiceUtils.exportReport(response, books, resource);
  }

  /**
   * Update book stock and availability based on message values.
   *
   * @param message the message from StockService.
   * @param availability the new availability.
   */
  private void updateBookFromMessage(String message, Availability availability) {

    Pair<Integer, Integer> pair = BookServiceUtils.readMessage(message);

    if (pair != null) {
      Book book =
          bookRepository
              .findById(pair.getFirst())
              .orElseThrow(() -> new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE));
      book.setStockAvailable(pair.getSecond());
      book.setAvailability(availability);
      Book updatedBook = bookRepository.save(book);

      LOGGER.log(
          Level.INFO,
          "Book updated: Availability: {}, stock: {}",
          updatedBook.getAvailability(),
          updatedBook.getStockAvailable());
    }
  }
}
