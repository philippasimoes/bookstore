package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookSampleDto;
import com.bookstore.catalog_service.model.entity.BookSample;
import org.mapstruct.Mapper;


import java.util.List;


@Mapper
public interface BookSampleMapper {

    BookSampleDto bookToBookSampleDto(BookSample bookSample);

    BookSample bookSampleDtoToBookSample(BookSampleDto bookSampleDto);

    List<BookSampleDto> bookSampleListToBookSampleDtoList(List<BookSample> bookSampleList);

    List<BookSample> bookSampleDtoListToBookSampleList(List<BookSampleDto> bookDtoList);
}
