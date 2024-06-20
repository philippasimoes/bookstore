package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.model.entity.Book;
import com.bookstore.catalog_service.model.entity.BookSample;
import com.bookstore.catalog_service.repository.BookSampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookSampleService {

    @Autowired
    BookSampleRepository bookSampleRepository;

    public BookSample addBookSample(Book book, String sample) {

        BookSample bookSample = new BookSample();
        bookSample.setBook(book);
        bookSample.setSample(sample);
        return bookSampleRepository.save(bookSample);
    }
}
