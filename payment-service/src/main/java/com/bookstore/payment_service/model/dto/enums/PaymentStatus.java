package com.bookstore.payment_service.model.dto.enums;

public enum PaymentStatus {
  PENDING, // This is a payment that has begun, but is not complete.  An example of this is someone
  // who has filled out the checkout form and then gone to PayPal for payment.  We have the
  // record of sale, but they haven’t completed their payment yet.
  COMPLETE, // This is a payment that has been paid and the product delivered to the customer.
  REFUNDED, // This is a payment where money has been transferred back to the customer and the
  // customer no longer has access to the product.
  FAILED, // This is a payment where the payment process failed, whether it be a credit card
  // rejection or some other error.
  CANCELLED,
  OTHER
}
