package com.bookstore.payment_service.controller;

import com.bookstore.payment_service.model.dto.StripePayment;
import com.bookstore.payment_service.model.dto.StripeToken;
import com.bookstore.payment_service.service.CreditCardPaymentProcessor;
import com.bookstore.payment_service.service.PaypalPaymentProcessor;
import com.bookstore.payment_service.service.StripePaymentProcessor;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

  private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

  @Autowired PaypalPaymentProcessor paypalPaymentProcessor;
  @Autowired CreditCardPaymentProcessor creditCardPaymentProcessor;
  @Autowired StripePaymentProcessor stripePaymentProcessor;

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
  public RedirectView createPayPalPayment(@RequestBody Map<String, Object> payPalPayment) {

    try {
      Payment payment = (Payment) paypalPaymentProcessor.createPayment(payPalPayment);

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

  // ########################################### STRIPE ###########################################
  // To test this use the test card number: 4242424242424242, to expMonth and expYear use any future
  // date. Use any three-digit CVC
  @PostMapping("/stripe/card/token")
  @ResponseBody
  public StripeToken createCardToken(@RequestBody StripeToken model) {

    return stripePaymentProcessor.createCardToken(model);
  }

  @PostMapping("/stripe/charge")
  @ResponseBody
  public ResponseEntity<StripePayment> charge(@RequestBody Map<String, Object> model) {
    try {
      return ResponseEntity.ok((StripePayment) stripePaymentProcessor.createPayment(model));
    } catch (StripeException e) {
      LOGGER.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // ################################### CREDIT CARD - TEST ONLY ###################################
  @PostMapping("/credit")
  public ResponseEntity<String> creditCardPayment(@RequestBody Map<String, Object> request) {
    return ResponseEntity.ok(creditCardPaymentProcessor.createPayment(request));
  }
}
