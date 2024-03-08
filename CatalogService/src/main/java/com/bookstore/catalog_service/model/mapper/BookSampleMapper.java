package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookSampleDto;
import com.bookstore.catalog_service.model.entity.BookSample;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Class to map BookSampleDto to BookSample and BookSample to BookSampleDto.
 *
 * @author Filipa Simões
 */
@Mapper
public interface BookSampleMapper {

    BookSampleDto bookToBookSampleDto(BookSample bookSample);

    BookSample bookSampleDtoToBookSample(BookSampleDto bookSampleDto);

    List<BookSampleDto> bookSampleListToBookSampleDtoList(List<BookSample> bookSampleList);

    List<BookSample> bookSampleDtoListToBookSampleList(List<BookSampleDto> bookDtoList);
}
