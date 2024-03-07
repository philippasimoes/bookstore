package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTagRepository extends JpaRepository<BookTag, Integer> {

    boolean existsByValue(String value);

    Optional<BookTag> findByValue(String value);
}
