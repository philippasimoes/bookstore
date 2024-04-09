package com.bookstore.return_service.model.mapper;

import com.bookstore.return_service.model.dto.ReturnItemDto;
import com.bookstore.return_service.model.entity.ReturnItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReturnItemMapper {

  @Mapping(target = "returnEntity", ignore = true)
  ReturnItem toEntity(ReturnItemDto returnItemDto);

  @Mapping(target = "returnEntity", ignore = true)
  ReturnItemDto toDto(ReturnItem returnItem);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  ReturnItem partialUpdate(ReturnItemDto returnItemDto, @MappingTarget ReturnItem returnItem);
}
