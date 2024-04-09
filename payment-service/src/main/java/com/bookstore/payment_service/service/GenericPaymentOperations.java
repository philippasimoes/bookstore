package com.bookstore.payment_service.service;

import com.bookstore.payment_service.model.entity.BasePayment;
import com.bookstore.payment_service.repository.BasePaymentRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericPaymentOperations {

  @Autowired BasePaymentRepository basePaymentRepository;

  public Map<String, String> getPaymentDetails(int orderId) {

    Optional<BasePayment> basePayment = basePaymentRepository.findByOrderId(orderId);

    Map<String, String> map = new HashMap<>();

    if (basePayment.isPresent()) {
      map.put("method", basePayment.get().getPaymentMethod().name());
      map.put("paymentDetails", (String) basePayment.get().getPaymentDetails().get("externalPaymentId"));
    }

    return map;
  }
}
