package com.bookstore.order_service.model.mapper;

import com.bookstore.order_service.model.dto.OrderDto;
import com.bookstore.order_service.model.entity.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

  Order toEntity(OrderDto orderDto);

  OrderDto toDto(Order order);

  List<Order> toEntityList(List<OrderDto> orderDtoList);

  List<OrderDto> toDtoList(List<Order> orderList);
}
