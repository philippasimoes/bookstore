package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.entity.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  List<Item> findByOrderId(/*@Param("orderId")*/ int orderId);

  Optional<Item> findByOrderIdAndBookId(int orderId, int bookId);
}
