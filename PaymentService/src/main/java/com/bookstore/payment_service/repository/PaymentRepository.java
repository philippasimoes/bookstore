package com.bookstore.payment_service.repository;

import com.bookstore.payment_service.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {}
