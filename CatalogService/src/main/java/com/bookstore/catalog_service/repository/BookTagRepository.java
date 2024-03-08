package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.BookTag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Book Tag repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface BookTagRepository extends JpaRepository<BookTag, Integer> {

  boolean existsByValue(String value);

  Optional<BookTag> findByValue(String value);
}
