package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.entity.Item;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  @Query(value = "SELECT * FROM orderservice.item WHERE order_id =:orderId", nativeQuery = true)
  List<Item> findByOrderId(@Param("orderId") int orderId);

  //@Query(value= "SELECT * FROM orderservice.itme WHERE order_id = :orderId AND book_id = :bookId", nativeQuery = true)
  Optional<Item> findByOrderIdAndBookId( int orderId, int bookId);
}

