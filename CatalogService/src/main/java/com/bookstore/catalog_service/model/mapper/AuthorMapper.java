package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.AuthorDto;
import com.bookstore.catalog_service.model.entity.Author;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;

/**
 * Class to map AuthorDto to Author and Author to AuthorDto.
 *
 * @author Filipa Simões
 */
@Mapper
public interface AuthorMapper {

  AuthorDto toDto(Author author);

  Author toEntity(AuthorDto authorDto);

  Set<AuthorDto> toDtoSet(Set<Author> authorSet);
  List<AuthorDto> toDtoList(List<Author> authorList);
}
