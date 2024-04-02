package com.bookstore.stock_service.repository;

import com.bookstore.stock_service.model.entity.Stock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Stock repository class.
 *
 * @author Filipa Simões
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

  Optional<Stock> findByBookId(int book_id);
}
