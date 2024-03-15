package com.bookstore.shipping_service.repository;

import com.bookstore.shipping_service.model.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {}
