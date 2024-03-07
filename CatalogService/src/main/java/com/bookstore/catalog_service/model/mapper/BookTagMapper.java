package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookTagMapper {
    BookTagDto bookTagToBookTagDto(BookTag bookTag);

    BookTag bookTagDtoToBookTag(BookTagDto bookTagDto);

    List<BookTagDto> tagLisToTagDtoList(List<BookTag> bookTagList);

    List<BookTag> bookTagDtoLisToBookTagList(List<BookTagDto> bookTagDtoList);
}
