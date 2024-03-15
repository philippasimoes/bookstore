package com.bookstore.return_service.model.mapper;

import com.bookstore.return_service.model.dto.ReturnDto;
import com.bookstore.return_service.model.entity.Return;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReturnMapper {

  Return toEntity(ReturnDto returnDto);

  ReturnDto toDto(Return returnEntity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Return partialUpdate(ReturnDto returnDto, @MappingTarget Return returnEntity);
}
