package com.bookstore.order_service.model.mapper;

import com.bookstore.order_service.model.dto.ItemDto;
import com.bookstore.order_service.model.entity.Item;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

  @Mapping(target = "order", ignore = true)
  Item toEntity(ItemDto itemDto);

  @Mapping(target = "order", ignore = true)
  ItemDto toDto(Item item);

  List<Item> toEntityList(List<ItemDto> itemDtoList);

  List<ItemDto> toDtoList(List<Item> itemList);
}
