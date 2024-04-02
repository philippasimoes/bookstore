package com.bookstore.shipping_service.model.mapper;

import com.bookstore.shipping_service.model.dto.ShipmentDto;
import com.bookstore.shipping_service.model.entity.Shipment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentMapper {

  Shipment toEntity(ShipmentDto shipmentDto);

  ShipmentDto toDto(Shipment shipment);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Shipment partialUpdate(ShipmentDto shipmentDto, @MappingTarget Shipment shipment);
}
