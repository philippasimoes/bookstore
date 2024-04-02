package com.bookstore.payment_service.controller;

import com.bookstore.payment_service.model.dto.OrderData;
import com.bookstore.payment_service.service.PaypalPaymentProcessor;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/payment")
public class PaymentController {

  private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

  @Autowired PaypalPaymentProcessor paypalPaymentProcessor;

  // ########################################### PAYPAL ###########################################

  // NOTE: to validate this is working without a frontend we need to send the request in a browser,
  // ideally Firefox
  // (so we can log in into the sandbox PayPal account). To do so, open the inspector and edit the
  // request with the needed information (url = localhost:10004/payment/paypal, method = POST,
  // Accept = */*, Content-Type = application/json, Body = an OrderData object).
  // ## Credentials ##
  // email: sb-cm9kt30002196@personal.example.com
  // password: 3UNPwC%k
  @PostMapping("/paypal")
  public RedirectView createPayPalPayment(@RequestBody OrderData request) {

    try {

      String cancelUrl = "http://localhost:10004/payment/paypal/cancel";
      String successUrl = "http://localhost:10004/payment/paypal/success";

      Payment payment =
          paypalPaymentProcessor.createPayment(
              Integer.parseInt(request.customerId()),
              Integer.parseInt(request.orderId()),
              Double.parseDouble(request.price()),
              "EUR",
              "paypal",
              "sale",
              "Payment Description",
              cancelUrl,
              successUrl);

      for (Links links : payment.getLinks()) {
        if (links.getRel().equals("approval_url")) {
          return new RedirectView(links.getHref());
        }
      }

    } catch (PayPalRESTException e) {
      LOGGER.error(e.getMessage());
    }
    return new RedirectView("/payment/paypal/error");
  }

  @GetMapping("/paypal/success")
  public String paymentSuccess(
      @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
    try {

      Payment payment = paypalPaymentProcessor.executePayment(paymentId, payerId);

      if (payment.getState().equals("approved")) {
        return "paymentSuccess";
      }
    } catch (PayPalRESTException e) {
      LOGGER.error(e.getMessage());
    }
    return "paymentSuccess";
  }

  @GetMapping("/paypal/cancel")
  public String paymentCancel() {
    return "paymentCancel";
  }

  @GetMapping("/paypal/error")
  public String paymentError() {
    return "paymentError";
  }
}
