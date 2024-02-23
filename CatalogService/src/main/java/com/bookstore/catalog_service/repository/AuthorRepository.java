package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer>, JpaSpecificationExecutor<Author> {

  //tem de ser com query (like)
  List<Author> findByName(String name);

}
