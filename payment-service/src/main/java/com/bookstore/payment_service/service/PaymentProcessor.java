package com.bookstore.payment_service.service;

import java.util.Map;

public interface PaymentProcessor {

  Object createPayment(Map<String, Object> request) throws Exception;

}
