package com.bookstore.payment_service.model.mapper;

import com.bookstore.payment_service.model.dto.PaymentDto;
import com.bookstore.payment_service.model.entity.BasePayment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

  BasePayment toEntity(PaymentDto paymentDto);

  PaymentDto toDto(BasePayment basePayment);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  BasePayment partialUpdate(PaymentDto paymentDto, @MappingTarget BasePayment basePayment);
}
