package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;
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
