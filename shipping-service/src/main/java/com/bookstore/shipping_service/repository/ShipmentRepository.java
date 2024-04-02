package com.bookstore.shipping_service.repository;

import com.bookstore.shipping_service.model.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

  Optional<Shipment> findByOrderId(int orderId);
}
