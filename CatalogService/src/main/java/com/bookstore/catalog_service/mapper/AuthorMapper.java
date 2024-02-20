package com.bookstore.catalog_service.mapper;

import com.bookstore.catalog_service.dto.AuthorDto;
import com.bookstore.catalog_service.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AuthorMapper {

    @Mapping(target="books", ignore = true)
    AuthorDto authorToAuthorDto(Author author);

    @Mapping(target="books", ignore = true)
    Author authorDtoToAuthor(AuthorDto authorDto);

    List<AuthorDto> authorLisToAuthorDtoList(List<Author> authorList);

    List<Author> authorDtoLisToAuthorList(List<AuthorDto> authorDtoList);
}
