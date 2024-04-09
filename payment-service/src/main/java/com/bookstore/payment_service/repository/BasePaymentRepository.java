package com.bookstore.payment_service.repository;

import com.bookstore.payment_service.model.entity.BasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasePaymentRepository extends JpaRepository<BasePayment, Integer> {

  @Query(
      value =
          "SELECT * FROM paymentservice.payment WHERE payment_details ->> 'externalPaymentId' = :externalPaymentId",
      nativeQuery = true)
  Optional<BasePayment> findByPaypalPaymentId(@Param("externalPaymentId") String paymentId);

  Optional<BasePayment> findByOrderId(int orderId);
}
