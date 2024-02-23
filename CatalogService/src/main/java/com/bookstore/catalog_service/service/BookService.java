package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.InputNotAcceptedException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.dto.TagDto;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.model.entity.BookSample;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.entity.Tag;
import com.bookstore.catalog_service.model.mapper.AuthorMapper;
import com.bookstore.catalog_service.model.mapper.BookMapper;
import com.bookstore.catalog_service.model.mapper.LanguageMapper;
import com.bookstore.catalog_service.model.mapper.TagMapper;
import com.bookstore.catalog_service.repository.AuthorRepository;
import com.bookstore.catalog_service.repository.BookRepository;
import com.bookstore.catalog_service.repository.BookSampleRepository;
import com.bookstore.catalog_service.repository.LanguageRepository;
import com.bookstore.catalog_service.repository.TagRepository;
import com.bookstore.catalog_service.specifications.BookSpecifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Book service class.
 *
 * @author Filipa Simões
 */
@Service
public class BookService {

    private final Logger LOGGER = LogManager.getLogger(BookService.class);

    @Autowired
    BookRepository bookRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookSampleRepository bookSampleRepository;
    @Autowired
    TagMapper tagMapper;
    @Autowired
    AuthorMapper authorMapper;
    @Autowired
    LanguageMapper languageMapper;
    @Autowired
    BookMapper bookMapper;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * Get all books.
     *
     * @return a list of BookDto.
     */
    public List<BookDto> getAllBooks() {

        return bookMapper.bookListToBookDtoList(bookRepository.findAll());

    }


    /**
     * Retrieve a list of books where availability is "Available".
     *
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksByAvailability(Availability availability) {

        return bookMapper.bookListToBookDtoList(
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
            return bookMapper.bookListToBookDtoList(
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
            return bookMapper.bookListToBookDtoList(
                    bookRepository.findAll(
                            Specification.where(BookSpecifications.allBooksFromLanguage(language.get()))));
        } else throw new ResourceNotFoundException("Author not found");
    }


    /**
     * Retrieve a list of books that have the same tag.
     *
     * @param tagId the tag identifier.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksByTag(int tagId) {

        Optional<Tag> tag = tagRepository.findById(tagId);

        if (tag.isPresent()) {
            return bookMapper.bookListToBookDtoList(
                    bookRepository.findAll(
                            Specification.where(BookSpecifications.allBooksFromTag(tag.get()))));
        } else throw new ResourceNotFoundException("Author not found");
    }


    /**
     * Retrieve a list of books that have the same genre.
     *
     * @param genre the book genre.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksByGenre(String genre) {

        return bookMapper.bookListToBookDtoList(
                bookRepository.findAll(
                        Specification.where(BookSpecifications.hasPredicate("genre", genre))));
    }


    /**
     * Retrieve a list of books that have the same category.
     *
     * @param category the book category.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksByCategory(String category) {

        return bookMapper.bookListToBookDtoList(
                bookRepository.findAll(
                        Specification.where(BookSpecifications.hasPredicate("category", category))));
    }


    /**
     * Retrieve a list of books that have the same collection.
     *
     * @param collection the book collection.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksByCollection(String collection) {

        return bookMapper.bookListToBookDtoList(
                bookRepository.findAll(
                        Specification.where(BookSpecifications.hasPredicate("collection", collection))));
    }


    /**
     * Retrieve a list of books that belongs to a series or not.
     *
     * @param isInASeries the boolean flag used.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksBySeries(boolean isInASeries) {

        return bookMapper.bookListToBookDtoList(
                bookRepository.findBookBySeries(isInASeries));
    }


    /**
     * Retrieve a list of books that are in a determined price range.
     *
     * @param startPrice the range lower limit.
     * @param endPrice   the range upper limit.
     * @return a list of BookDto.
     */
    public List<BookDto> getBooksInPriceRange(double startPrice, double endPrice) {

        if (endPrice > startPrice) {
            return bookMapper.bookListToBookDtoList(bookRepository.findByPriceBetween(startPrice, endPrice));
        } else throw new InputNotAcceptedException("The start price should bw lower than the end price");

    }


    /**
     * Validates if a book exists in database.
     *
     * @param id the book identifier.
     * @return a boolean.
     */
    public boolean bookExistsById(int id) {

        return bookRepository.existsById(id);
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

        return bookMapper.bookToBookDto(book);
    }


    /**
     * Add new book to the database.
     *
     * @param bookDto the book (data transfer object) sent in request.
     * @return the persisted book (entity).
     */
    public Book addNewBook(BookDto bookDto) {

        if (bookRepository.findByIsbn(bookDto.getIsbn()) != null) {
            throw new DuplicatedResourceException(
                    "Book with ISBN " + bookDto.getIsbn() + "already exists");
        } else {

            Book createdBook = bookRepository.save(bookMapper.bookDtoToBook(bookDto));

            Set<Language> languageSet = getLanguageFromBook(bookDto);
            Set<Tag> tagSet = getTagsFromBook(bookDto);
            Set<Author> authorSet = getAuthorsFromBook(bookDto);

            createdBook.setLanguages(languageSet);
            createdBook.setTags(tagSet);
            createdBook.setAuthors(authorSet);

            return bookRepository.save(createdBook);
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

        if (bookRepository.findById(bookId).isPresent()) {
            BookSample bookSample = new BookSample();
            bookSample.setBookId(bookId);
            bookSample.setSample(sample);
            bookSampleRepository.save(bookSample);
            return "Book sample added to book with ID" + bookId;
        } else throw new ResourceNotFoundException("Book not found");
    }


    /**
     * Update book availability.
     *
     * @param id           the book identifier.
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
            if (!authorRepository.existsById(authorDto.getId())) {
                Author savedAuthor = authorRepository.save(authorMapper.authorDtoToAuthor(authorDto));
                authorSet.add(savedAuthor);
            } else if (authorRepository.findById(authorDto.getId()).isPresent()) {
                authorSet.add(authorRepository.findById(authorDto.getId()).get());
            } else throw new ResourceNotFoundException("Author not found");
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
    private Set<Tag> getTagsFromBook(BookDto bookDto) {

        Set<Tag> tagSet = new HashSet<>();
        for (TagDto tagDto : bookDto.getTags()) {
            if (!tagRepository.existsByValue(tagDto.getValue())) {
                Tag savedTag = tagRepository.save(tagMapper.tagDtoToTag(tagDto));
                tagSet.add(savedTag);
            } else if (tagRepository.findByValue(tagDto.getValue()).isPresent()) {
                tagSet.add(tagRepository.findByValue(tagDto.getValue()).get());
            } else throw new ResourceNotFoundException("Tag not found");
        }
        return tagSet;
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

        for (LanguageDto language : bookDto.getLanguages()) {
            if (!languageRepository.existsByCode(language.getCode())) {
                Language savedLanguage =
                        languageRepository.save(languageMapper.languageDtoToLanguage(language));
                languageSet.add(savedLanguage);
            } else if (languageRepository.findByCode(language.getCode()).isPresent()) {
                languageSet.add(languageRepository.findByCode(language.getCode()).get());
            } else throw new ResourceNotFoundException("Language not found");
        }
        return languageSet;
    }


    /**
     * Update book stock and availability based on message values.
     *
     * @param message      the message from StockService.
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
            LOGGER.info(String.format("Book updated: Availability: %s, stock: %s",
                    updatedBook.getAvailability(), updatedBook.getStockAvailable()));

        } catch (JsonProcessingException e) {
            LOGGER.error("Could not read pair from message", e);
        }
    }

}
