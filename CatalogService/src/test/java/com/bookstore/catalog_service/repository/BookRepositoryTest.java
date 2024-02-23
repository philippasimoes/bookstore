package com.bookstore.catalog_service.repository;


import com.bookstore.catalog_service.CatalogServiceApplication;
import com.bookstore.catalog_service.model.dto.enums.Availability;
import com.bookstore.catalog_service.model.dto.enums.Format;
import com.bookstore.catalog_service.model.entity.Author;
import com.bookstore.catalog_service.model.entity.Book;

import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.specifications.BookSpecifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestPropertySource(
        locations = "classpath:application-test.properties")
@ContextConfiguration(classes = CatalogServiceApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    public BookRepository bookRepository;

    @Autowired
    public AuthorRepository authorRepository;

    @Autowired
    public LanguageRepository languageRepository;

    public Book book1;
    public Book book2;
    public Author author;
    public Author savedAuthor;
    public Language language;
    public Set<Author> authorList;
    public Set<Language> languageList;
    public List<Book> bookList;

    /**
     * Creates all needed resources.
     */
    @Before
    public void setup() {
        bookList = new ArrayList<>();
        authorList = new HashSet<>();
        languageList = new HashSet<>();

        author = new Author();
        author.setName("author");
        author.setAbout("about author");
        savedAuthor = authorRepository.save(author);
        authorList.add(savedAuthor);

        language = new Language();
        language.setCode("PT");
        Language savedLanguage = languageRepository.save(language);
        languageList.add(savedLanguage);

        book1 = new Book();
        book1.setIsbn("12345678");
        book1.setPrice(10.0);
        book1.setLanguages(languageList);
        book1.setAuthors(authorList);
        book1.setTitle("book_title");
        book1.setGenre("Adventure");
        book1.setAvailability(Availability.ON_ORDER);
        book1.setStockAvailable(10);
        bookRepository.save(book1);
        bookList.add(bookRepository.save(book1));

        book2 = new Book();
        book2.setIsbn("789456123");
        book2.setPrice(25.0);
        book2.setLanguages(languageList);
        book2.setAuthors(authorList);
        book2.setTitle("book_title_2");
        book2.setGenre("Terror");
        book2.setAvailability(Availability.AVAILABLE);
        book2.setFormat(Format.HARDCOVER);
        book2.setStockAvailable(10);
        bookRepository.save(book2);
        bookList.add(bookRepository.save(book2));

        savedAuthor.setBooks(bookList);
        authorRepository.save(savedAuthor);
        savedLanguage.setBooks(bookList);
        languageRepository.save(savedLanguage);

    }

    /**
     * Test if it's possible to update the book availability.
     */
    @Test
    public void testUpdateBookAvailability() {

        //Act
        bookRepository.updateBookAvailability(book1.getId(), Availability.AVAILABLE);

        //Assert
        assertEquals(Availability.AVAILABLE, bookRepository.findById(1).get().getAvailability());
    }

    /**
     * Test if it's possible to retrieve all books by availability.
     */
    @Test
    public void testHasAvailability() {
        //Act
        List<Book> books = bookRepository.findAll(
                Specification.where(BookSpecifications.hasAvailability(Availability.AVAILABLE)));

        //Assert
        assertEquals(bookRepository.findByIsbn("789456123").getTitle(), books.get(0).getTitle());
    }


    /**
     * Test if it's possible to retrieve all books from a certain author.
     */
    @Test
    public void testAllBooksFromAuthor() {
        //Act
        List<Book> books = bookRepository.findAll(Specification.where(BookSpecifications.allBooksFromAuthor(savedAuthor)));

        //Assert
        assertEquals(2, books.size());
    }


    /**
     * Test if it's possible to retrieve all books from a certain predicate (column and column value).
     */
    @Test
    public void testHasPredicate() {
        //Act
        List<Book> books = bookRepository.findAll(Specification.where(BookSpecifications.hasPredicate("genre", "Adventure")));

        //Assert
        assertEquals(1, books.size());
    }
}
