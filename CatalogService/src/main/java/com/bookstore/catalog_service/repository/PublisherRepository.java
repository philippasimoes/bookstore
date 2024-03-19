package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

    Optional<Publisher> findByName(String name);
}
