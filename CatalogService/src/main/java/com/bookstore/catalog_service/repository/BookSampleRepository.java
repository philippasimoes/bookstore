package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.BookSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookSampleRepository extends JpaRepository<BookSample, Integer> {
}
