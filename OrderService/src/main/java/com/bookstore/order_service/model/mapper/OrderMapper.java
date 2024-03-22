package com.bookstore.order_service.model.mapper;

import com.bookstore.order_service.model.dto.OrderDto;
import com.bookstore.order_service.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

  @Mapping(target="items", ignore = true)
  Order toEntity(OrderDto orderDto);

  @Mapping(target="items", ignore = true)
  OrderDto toDto(Order order);
  
}
