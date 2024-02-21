package com.bookstore.catalog_service.repository;

import com.bookstore.catalog_service.dto.enums.Availability;
import com.bookstore.catalog_service.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

  Book findByIsbn(String isbn);
  List<Book> findBookBySeries(boolean series);

  List<Book> findByPriceBetween(double startPrice, double endPrice);

  @Transactional
  @Modifying
  @Query("update Book a set a.availability = :availability where a.id = :id")
  void updateBookAvailability(
      @Param("id") int id, @Param("availability") Availability availability);

}
