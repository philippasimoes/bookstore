package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.model.mapper.BookTagMapper;
import com.bookstore.catalog_service.repository.BookTagRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Book Tag service class.
 *
 * @author Filipa Simões
 */
@Service
public class BookTagService {

  private static final Logger LOGGER = LogManager.getLogger(BookTagService.class);
  private final BookTagRepository bookTagRepository;
  private final BookTagMapper bookTagMapper;

  public BookTagService(BookTagRepository bookTagRepository, BookTagMapper bookTagMapper) {

    this.bookTagRepository = bookTagRepository;
    this.bookTagMapper = bookTagMapper;
  }

  public Set<BookTagDto> getAllBookTags() {
    return bookTagMapper.toDtoSet(new HashSet<>(bookTagRepository.findAll()));
  }

  @Transactional
  public BookTag addNewBookTag(BookTagDto bookTagDto) {
    if (bookTagRepository.existsByValue(bookTagDto.getValue())) {

      LOGGER.log(Level.ERROR, "Tag {} already exists in database.", bookTagDto.getValue());
      throw new DuplicatedResourceException("Tag already exists.");
    } else return bookTagRepository.save(bookTagMapper.toEntity(bookTagDto));
  }
}
