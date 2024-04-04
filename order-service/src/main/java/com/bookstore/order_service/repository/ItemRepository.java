package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.entity.Item;
import com.bookstore.order_service.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  @Query(value = "SELECT * FROM orderservice.items WHERE 'order_id' =:orderId", nativeQuery = true)
  List<Item> foundByOrderId(@Param("orderId") int orderId);
}
