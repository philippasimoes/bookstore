package com.bookstore.catalog_service.repository;


import com.bookstore.catalog_service.CatalogServiceApplication;
import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.model.Book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

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

    public Book book;

    @Before
    public void setup() {
        book = new Book();
        book.setIsbn("12345678");
        book.setPrice(10.0);
        book.setLanguages(new HashSet<>());
        book.setAuthors(new HashSet<>());
        book.setTitle("book_title");
        book.setAvailability(Availability.ON_ORDER);
        book.setStockAvailable(10);
        bookRepository.save(book);
    }

    @Test
    public void testUpdateBookAvailability() {
        bookRepository.updateBookAvailability(book.getId(), Availability.AVAILABLE);

        assertEquals(Availability.AVAILABLE, bookRepository.findById(1).get().getAvailability());
    }
}
