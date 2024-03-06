package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AuthorMapper {

    AuthorDto authorToAuthorDto(Author author);

    Author authorDtoToAuthor(AuthorDto authorDto);

    List<AuthorDto> authorLisToAuthorDtoList(List<Author> authorList);

    List<Author> authorDtoLisToAuthorList(List<AuthorDto> authorDtoList);
}
