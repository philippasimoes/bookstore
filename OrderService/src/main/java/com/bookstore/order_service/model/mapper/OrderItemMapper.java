package com.bookstore.order_service.model.mapper;

import com.bookstore.order_service.model.dto.OrderItemDto;
import com.bookstore.order_service.model.entity.OrderItem;
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
public interface OrderItemMapper {

  OrderItem toEntity(OrderItemDto orderItemDto);

  OrderItemDto toDto(OrderItem orderItem);

  List<OrderItem> toEntityList(List<OrderItemDto> orderItemDtoList);

  List<OrderItemDto> toDtoList(List<OrderItem> orderItemList);
}
