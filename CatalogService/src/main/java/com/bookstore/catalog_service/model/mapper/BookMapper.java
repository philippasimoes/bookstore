package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BookMapper {

    @Mapping(target="authors", ignore = true)
    @Mapping(target="tags", ignore = true)
    @Mapping(target="languages", ignore = true)
    BookDto bookToBookDto(Book book);

    @Mapping(target="authors", ignore = true)
    @Mapping(target="tags", ignore = true)
    @Mapping(target="languages", ignore = true)
    Book bookDtoToBook(BookDto bookDto);

    List<BookDto> bookListToBookDtoList(List<Book> bookList);

    List<Book> bookDtoListToBookList(List<BookDto> bookDtoList);
}
