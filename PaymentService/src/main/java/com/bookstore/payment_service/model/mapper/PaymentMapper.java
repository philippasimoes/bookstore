package com.bookstore.payment_service.model.mapper;

import com.bookstore.payment_service.model.dto.PaymentDto;
import com.bookstore.payment_service.model.entity.Payment;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentMapper {
  PaymentDto paymentToPaymentDto(Payment payment);

  Payment paymentDtoToPayment(PaymentDto paymentDto);

  List<PaymentDto> paymentListToPaymentDtoList(List<Payment> paymentList);

  List<Payment> paymentDtoListToPaymentList(List<PaymentDto> paymentList);
}
