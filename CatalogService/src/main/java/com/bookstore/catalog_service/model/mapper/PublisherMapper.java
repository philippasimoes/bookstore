package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.PublisherDto;
import com.bookstore.catalog_service.model.entity.Publisher;
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
public interface PublisherMapper {

  Publisher toEntity(PublisherDto publisherDto);

  PublisherDto toDto(Publisher publisher);

}
