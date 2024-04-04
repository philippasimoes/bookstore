package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.entity.Order;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

  List<Order> findByCustomerId(int customerId);

  @Query(
      value =
          "SELECT * FROM orderservice.order o WHERE o.customer_id = :customerId AND o.status = :orderStatus AND o.shipment_date BETWEEN :initialShippingDate AND :finalShippingDate",
      nativeQuery = true)
  List<Order> findByCustomerIdAndShipmentDate(
      @Param("customerId") int customerId,
      @Param("orderStatus") String orderStatus,
      @Param("initialShippingDate") Timestamp initialShippingDate,
      @Param("finalShippingDate") Timestamp finalShippingDate);
}
