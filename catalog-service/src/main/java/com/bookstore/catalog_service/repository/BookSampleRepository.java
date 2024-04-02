package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.BookSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Book Sample repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface BookSampleRepository extends JpaRepository<BookSample, Integer> {}
