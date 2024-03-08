package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Class to map AuthorDto to Author and Author to AuthorDto.
 *
 * @author Filipa Simões
 */
@Mapper
public interface AuthorMapper {

  AuthorDto authorToAuthorDto(Author author);

  Author authorDtoToAuthor(AuthorDto authorDto);

  List<AuthorDto> authorLisToAuthorDtoList(List<Author> authorList);

  List<Author> authorDtoLisToAuthorList(List<AuthorDto> authorDtoList);
}
