package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    boolean existsByValue(String value);

    Optional<Tag> findByValue(String value);
}
