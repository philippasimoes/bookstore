package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
    boolean existsByCode(String code);

    Optional<Language> findByCode(String code);
}
