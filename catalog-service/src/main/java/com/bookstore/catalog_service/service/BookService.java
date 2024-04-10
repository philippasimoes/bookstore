package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.InputNotAcceptedException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.model.entity.BookSample;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.entity.Publisher;
import com.bookstore.catalog_service.model.mapper.AuthorMapper;
import com.bookstore.catalog_service.model.mapper.BookMapper;
import com.bookstore.catalog_service.model.mapper.BookTagMapper;
import com.bookstore.catalog_service.model.mapper.LanguageMapper;
import com.bookstore.catalog_service.repository.AuthorRepository;
import com.bookstore.catalog_service.repository.BookRepository;
import com.bookstore.catalog_service.repository.BookSampleRepository;
import com.bookstore.catalog_service.repository.BookTagRepository;
import com.bookstore.catalog_service.repository.LanguageRepository;
import com.bookstore.catalog_service.repository.PublisherRepository;
import com.bookstore.catalog_service.specifications.BookSpecifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Book service class.
 *
 * @author Filipa Simões
 */
@Service
@Transactional
public class BookService {

  private static final Logger LOGGER = LogManager.getLogger(BookService.class);

  @Autowired BookRepository bookRepository;
  @Autowired LanguageRepository languageRepository;
  @Autowired BookTagRepository bookTagRepository;
  @Autowired AuthorRepository authorRepository;
  @Autowired BookSampleRepository bookSampleRepository;
  @Autowired PublisherRepository publisherRepository;
  @Autowired BookTagMapper bookTagMapper;
  @Autowired AuthorMapper authorMapper;
  @Autowired LanguageMapper languageMapper;
  @Autowired BookMapper bookMapper;
  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplate restTemplate;
  @Autowired ApplicationContext context;

  private static final String TOKEN_URL =
      "http://keycloak:8080/realms/bookstore/protocol/openid-connect/token";

  private static final String STOCK_SERVICE_ID = "stock-service";

  private static final String STOCK_SERVICE_SECRET = "vzFYf3wn4yItcZ35vKJZf63VmYC4TOSx";

  private static final String GRANT_TYPE = "client_credentials";

  private static final String STOCK_CREATION_URL = "http://stock-service/stock/book/";

  /**
   * Get all books.
   *
   * @return a list of BookDto.
   */
  public List<BookDto> getAllBooks() {

    List<BookDto> bookDtos = new ArrayList<>();

    for (Book book : bookRepository.findAll()) {
      BookDto bookDto = bookMapper.toDto(book);
      bookDto.setBookTags(bookTagMapper.toDtoSet(book.getBookTags()));
      bookDto.setAuthors(authorMapper.toDtoSet(book.getAuthors()));
      bookDto.setLanguages(languageMapper.toDtoSet(book.getLanguages()));

      bookDtos.add(bookDto);
    }
    return bookDtos;
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
   * @param author_id the author identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByAuthor(int author_id) {

    Optional<Author> author = authorRepository.findById(author_id);

    if (author.isPresent()) {
      return bookMapper.toDtoList(
          bookRepository.findAll(
              Specification.where(BookSpecifications.allBooksFromAuthor(author.get()))));
    } else throw new ResourceNotFoundException("Author not found");
  }

  /**
   * Retrieve a list of books that have the same language.
   *
   * @param language_id the language identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByLanguage(int language_id) {

    Optional<Language> language = languageRepository.findById(language_id);

    if (language.isPresent()) {
      return bookMapper.toDtoList(
          bookRepository.findAll(
              Specification.where(BookSpecifications.allBooksFromLanguage(language.get()))));
    } else throw new ResourceNotFoundException("Author not found");
  }

  /**
   * Retrieve a list of books that have the same tag.
   *
   * @param bookTagId the tag identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByTag(int bookTagId) {

    Optional<BookTag> bookTag = bookTagRepository.findById(bookTagId);

    if (bookTag.isPresent()) {
      return bookMapper.toDtoList(
          bookRepository.findAll(
              Specification.where(BookSpecifications.allBooksFromTag(bookTag.get()))));
    } else throw new ResourceNotFoundException("Author not found");
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
  public BookDto getBookByID(int id) {

    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

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
      Publisher publisher = getPublisherFromBook(bookDto);
      Set<Language> languageSet = getLanguageFromBook(bookDto);
      Set<BookTag> bookTagSet = getTagsFromBook(bookDto);
      Set<Author> authorSet = getAuthorsFromBook(bookDto);

      Book newBook = bookMapper.toEntity(bookDto);
      newBook.setPublisher(publisher);
      newBook.setLanguages(languageSet);
      newBook.setBookTags(bookTagSet);
      newBook.setAuthors(authorSet);

      Book bookWithLists = bookRepository.save(newBook);

      // create stock entry for this book - the entry is created with 0 units.
      createStock(bookWithLists.getId());

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

      if (!getLanguageFromBook(bookDto).isEmpty()) {
        updatedBook.setLanguages(getLanguageFromBook(bookDto));
      }

      if (!getTagsFromBook(bookDto).isEmpty()) {
        updatedBook.setBookTags(getTagsFromBook(bookDto));
      }

      if (!getAuthorsFromBook(bookDto).isEmpty()) {
        updatedBook.setAuthors(getAuthorsFromBook(bookDto));
      }

      return bookRepository.save(updatedBook);

    } else {
      throw new ResourceNotFoundException("Book not found in database");
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
      BookSample bookSample = new BookSample();
      bookSample.setBook(book.get());
      bookSample.setSample(sample);
      bookSampleRepository.save(bookSample);
      return "Book sample added to book with ID" + bookId;
    } else throw new ResourceNotFoundException("Book not found");
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
    } else throw new ResourceNotFoundException("Book not found");
  }

  /**
   * Consume messages in updated queue.
   *
   * @param message the message from StockService.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.updated.name}")
  public void consumeUpdatedEvents(String message) {

    LOGGER.info(String.format("received message [%s]", message));

    updateBookFromMessage(message, Availability.AVAILABLE);
  }

  /**
   * Consume messages in sold out queue.
   *
   * @param message the message from StockService.
   */
  @RabbitListener(queues = "${rabbitmq.queue.event.soldout.name}")
  public void consumeSoldOutEvents(String message) {

    LOGGER.info(String.format("received message [%s]", message));

    updateBookFromMessage(message, Availability.SOLD_OUT);
  }

  /**
   * Auxiliary method that get the authors from a book - if the author does not exist in database it
   * will be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of authors.
   */
  private Set<Author> getAuthorsFromBook(BookDto bookDto) {

    Set<Author> authorSet = new HashSet<>();

    for (AuthorDto authorDto : bookDto.getAuthors()) {
      Optional<Author> author = authorRepository.findByIsni(authorDto.getIsni());

      if (author.isPresent()) {
        authorSet.add(author.get());
      } else {
        Author savedAuthor = authorRepository.save(authorMapper.toEntity(authorDto));
        authorSet.add(savedAuthor);
      }
    }

    return authorSet;
  }

  /**
   * Auxiliary method that get the tags from a book - if the tag does not exist in database it will
   * be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of tags.
   */
  private Set<BookTag> getTagsFromBook(BookDto bookDto) {

    Set<BookTag> bookTagSet = new HashSet<>();

    for (BookTagDto bookTagDto : bookDto.getBookTags()) {
      Optional<BookTag> bookTag = bookTagRepository.findByValue(bookTagDto.getValue());

      if (bookTag.isPresent()) {
        bookTagSet.add(bookTag.get());
      } else {
        BookTag savedBookTag = bookTagRepository.save(bookTagMapper.toEntity(bookTagDto));
        bookTagSet.add(savedBookTag);
      }
    }
    return bookTagSet;
  }

  /**
   * Auxiliary method that get the languages from a book - if the language does not exist in
   * database it will be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of languages.
   */
  private Set<Language> getLanguageFromBook(BookDto bookDto) {

    Set<Language> languageSet = new HashSet<>();

    for (LanguageDto languageDto : bookDto.getLanguages()) {
      Optional<Language> language = languageRepository.findByCode(languageDto.getCode());

      if (language.isPresent()) {
        languageSet.add(language.get());
      } else {
        Language savedLanguage = languageRepository.save(languageMapper.toDto(languageDto));
        languageSet.add(savedLanguage);
      }
    }
    return languageSet;
  }

  private Publisher getPublisherFromBook(BookDto bookDto) {
    Optional<Publisher> publisher = publisherRepository.findByName(bookDto.getPublisher().getName());

    if (publisher.isPresent()) {
      return publisher.get();
    } else {
      Publisher newPublisher = new Publisher();
      newPublisher.setName(bookDto.getPublisher().getName());
      newPublisher.setEmail(bookDto.getPublisher().getEmail());
      newPublisher.setPhoneNumber(bookDto.getPublisher().getPhoneNumber());
      return publisherRepository.save(newPublisher);
    }
  }

  /**
   * Update book stock and availability based on message values.
   *
   * @param message the message from StockService.
   * @param availability the new availability.
   */
  private void updateBookFromMessage(String message, Availability availability) {

    Pair<Integer, Integer> pair;
    try {
      pair = objectMapper.readValue(message, Pair.class);

      Book book = bookRepository.findById(pair.getFirst()).get();
      book.setStockAvailable(pair.getSecond());
      book.setAvailability(availability);
      Book updatedBook = bookRepository.save(book);
      LOGGER.info(
          String.format(
              "Book updated: Availability: %s, stock: %s",
              updatedBook.getAvailability(), updatedBook.getStockAvailable()));

    } catch (JsonProcessingException e) {
      LOGGER.error("Could not read pair from message", e);
    }
  }

  /**
   * Create a new stock entry with the specified book identifier. Note that this method should only
   * be called when creating a new book because the stock is created with zero units.
   *
   * @param bookId the book identifier.
   */
  private void createStock(int bookId) {

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set(
          "Authorization",
          "Bearer "
              + objectMapper
                  .readValue(authenticateAndGetJwtToken(), Map.class)
                  .get("access_token"));

      HttpEntity<String> requestEntity = new HttpEntity<>(headers);

      restTemplate.exchange(
          STOCK_CREATION_URL + bookId, HttpMethod.POST, requestEntity, String.class);

    } catch (JsonProcessingException e) {
      LOGGER.error(e.getMessage());
    }
  }

  /**
   * Authenticate catalog service in stock service with client id and client secret.
   *
   * @return the Jwt.
   */
  private String authenticateAndGetJwtToken() {

    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String requestBody =
        "grant_type="
            + GRANT_TYPE
            + "&client_id="
            + STOCK_SERVICE_ID
            + "&client_secret="
            + STOCK_SERVICE_SECRET;

    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, authHeaders);

    RestTemplate keycloakRestTemplate = new RestTemplate();

    ResponseEntity<String> response =
        keycloakRestTemplate.postForEntity(TOKEN_URL, requestEntity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      LOGGER.info("Authentication successful.");
      return response.getBody();
    } else {
      LOGGER.error(String.format("Authentication failure. Status: %s.", response.getStatusCode()));
      return null;
    }
  }

  public void exportReport(HttpServletResponse response) throws JRException, IOException {
    String path = "classpath:reports";

    List<Book> books = bookRepository.findAll();

    Resource resource = context.getResource("classpath:books.jrxml");

    InputStream inputStream = resource.getInputStream();
    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(books);
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

    response.setContentType(MediaType.APPLICATION_PDF_VALUE);
    response.setHeader("Content-Disposition", "inline; filename=books.pdf");
    final OutputStream outStream = response.getOutputStream();
    JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
  }
}
