package com.bookstore.payment_service.repository;

import com.bookstore.payment_service.model.entity.BasePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<BasePayment, Integer> {}
