package com.bookstore.return_service.model.mapper;

import com.bookstore.return_service.model.dto.ReturnDto;
import com.bookstore.return_service.model.entity.Return;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReturnMapper {

  @Mapping(target = "returnItems", ignore = true)
  Return toEntity(ReturnDto returnDto);

  @Mapping(target = "returnItems", ignore = true)
  ReturnDto toDto(Return returnEntity);

  List<ReturnDto> toDtoList(List<Return> returnList);
}
