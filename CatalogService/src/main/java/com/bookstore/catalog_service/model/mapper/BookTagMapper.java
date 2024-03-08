package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Class to map BookTagDto to BookTag and BookTag to BookTagDto.
 *
 * @author Filipa Simões
 */
@Mapper
public interface BookTagMapper {
  BookTagDto bookTagToBookTagDto(BookTag bookTag);

  BookTag bookTagDtoToBookTag(BookTagDto bookTagDto);

  List<BookTagDto> tagLisToTagDtoList(List<BookTag> bookTagList);

  List<BookTag> bookTagDtoLisToBookTagList(List<BookTagDto> bookTagDtoList);
}
