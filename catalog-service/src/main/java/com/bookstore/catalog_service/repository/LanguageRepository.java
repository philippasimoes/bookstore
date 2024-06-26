package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.Language;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Language repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
  boolean existsByCode(String code);

  Optional<Language> findByCode(String code);
}
