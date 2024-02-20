package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.dto.AuthorDto;
import com.bookstore.catalog_service.dto.BookDto;
import com.bookstore.catalog_service.dto.LanguageDto;
import com.bookstore.catalog_service.dto.TagDto;
import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.mapper.AuthorMapper;
import com.bookstore.catalog_service.mapper.BookMapper;
import com.bookstore.catalog_service.mapper.LanguageMapper;
import com.bookstore.catalog_service.mapper.TagMapper;
import com.bookstore.catalog_service.model.Author;
import com.bookstore.catalog_service.model.Book;
import com.bookstore.catalog_service.model.Language;
import com.bookstore.catalog_service.model.Tag;
import com.bookstore.catalog_service.repository.AuthorRepository;
import com.bookstore.catalog_service.repository.BookRepository;

import java.util.*;

import com.bookstore.catalog_service.repository.LanguageRepository;
import com.bookstore.catalog_service.repository.TagRepository;
import com.bookstore.catalog_service.specifications.BookSpecifications;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Book service class.
 *
 * @author Filipa Simões
 */
@Service
public class BookService {

  @Autowired BookRepository bookRepository;
  @Autowired LanguageRepository languageRepository;
  @Autowired TagRepository tagRepository;
  @Autowired AuthorRepository authorRepository;
  @Autowired TagMapper tagMapper;
  @Autowired AuthorMapper authorMapper;
  @Autowired LanguageMapper languageMapper;
  @Autowired BookMapper bookMapper;

  /**
   * Get book from database using the unique identifier.
   *
   * @param id the unique identifier.
   * @return the book object.
   */
  public BookDto getBookByID(int id) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    BookDto bookDto = new BookDto();
    BeanUtils.copyProperties(book, bookDto);
    return bookDto;
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
   * @param tag_id the tag identifier.
   * @return a list of BookDto.
   */
  public List<BookDto> getBooksByTag(int tag_id) {

    Optional<Tag> tag = tagRepository.findById(tag_id);

    if (tag.isPresent()) {
      return bookMapper.bookListToBookDtoList(
          bookRepository.findAll(
              Specification.where(BookSpecifications.allBooksFromTag(tag.get()))));
    } else throw new ResourceNotFoundException("Author not found");
  }

  public List<BookDto> getBooksByGenre(String genre) {
    return bookMapper.bookListToBookDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasPredicate("genre", genre))));
  }

  public List<BookDto> getBooksByCategory(String category) {
    return bookMapper.bookListToBookDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasPredicate("category", category))));
  }

  public List<BookDto> getBooksByCollection(String collection) {
    return bookMapper.bookListToBookDtoList(
        bookRepository.findAll(
            Specification.where(BookSpecifications.hasPredicate("collection", collection))));
  }

  public List<BookDto> getBooksBySeries(boolean series) {
    return bookMapper.bookListToBookDtoList(
        bookRepository.findBookBySeries(series));
  }

  public List<BookDto> getBooksInPriceRange(double startPrice, double endPrice){
    return bookMapper.bookListToBookDtoList(bookRepository.findByPriceBetween(startPrice, endPrice));
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
}
