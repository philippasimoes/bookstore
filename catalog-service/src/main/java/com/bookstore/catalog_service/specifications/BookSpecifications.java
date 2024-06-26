package com.bookstore.catalog_service.specifications;

import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.entity.Publisher;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for book entity.
 *
 * @author Filipa Simões
 */
public class BookSpecifications {

  /**
   * Used to query the database to retrieve the books with a determined availability.
   *
   * @param availability the availability.
   * @return A specification to be used to get a list of books with a determined availability.
   */
  public static Specification<Book> hasAvailability(Availability availability) {
    return (book, query, criteriaBuilder) ->
        criteriaBuilder.equal(book.get("availability"), availability);
  }

  public static Specification<Book> hasValue(String columnName, String value) {
    return (book, query, criteriaBuilder) -> criteriaBuilder.equal(book.get(columnName), value);
  }

  /**
   * Used to get all the books from a determined author (it will query the join table book_author).
   *
   * @param author the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromAuthor(Author author) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Author> authorsBooks = book.join("authors");
      return criteriaBuilder.equal(authorsBooks.get("id"), author.getId());
    };
  }

  /**
   * Used to get all the books from a determined publisher.
   *
   * @param publisher the publisher.
   * @return a specification to be used to get a list of books from the determined publisher.
   */
  public static Specification<Book> allBooksFromPublisher(Publisher publisher) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Publisher> publisherBooks = book.join("publishers");
      return criteriaBuilder.equal(publisherBooks.get("id"), publisher.getId());
    };
  }

  /**
   * Used to get all the books from a determined language (it will query the join table
   * book_language).
   *
   * @param language the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromLanguage(Language language) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Language> languageBooks = book.join("languages");
      return criteriaBuilder.equal(languageBooks.get("id"), language.getId());
    };
  }

  /**
   * Used to get all the books from a determined language (it will query the join table
   * book_language).
   *
   * @param language the author.
   * @return a specification to be used to get a list of books from the determined author.
   */
  public static Specification<Book> allBooksFromTag(BookTag language) {
    return (book, query, criteriaBuilder) -> {
      Join<Book, Language> languageBooks = book.join("languages");
      return criteriaBuilder.equal(languageBooks.get("id"), language.getId());
    };
  }
}
