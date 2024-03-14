package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Author repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface AuthorRepository
    extends JpaRepository<Author, Integer>, JpaSpecificationExecutor<Author> {

  // tem de ser com query (like)
  List<Author> findByName(String name);

  Author findByIsni(String isni);
}
