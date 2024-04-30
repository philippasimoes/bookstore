package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import com.bookstore.catalog_service.model.mapper.BookTagMapper;
import com.bookstore.catalog_service.repository.BookTagRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Book Tag service class.
 *
 * @author Filipa Simões
 */
@Service
public class BookTagService {

  private static final Logger LOGGER = LogManager.getLogger(BookTagService.class);

  @Autowired BookTagRepository bookTagRepository;
  @Autowired BookTagMapper bookTagMapper;

  public BookTag getTagById(int id) {

    return bookTagRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tag not found."));
  }

  public Set<BookTagDto> getAllBookTags() {
    return bookTagMapper.toDtoSet(new HashSet<>(bookTagRepository.findAll()));
  }

  public BookTag addNewBookTag(BookTagDto bookTagDto) {
    if (bookTagRepository.existsByValue(bookTagDto.getValue())) {

      LOGGER.log(Level.ERROR, "Tag {} already exists in database.", bookTagDto.getValue());
      throw new DuplicatedResourceException("Tag already exists.");
    } else return bookTagRepository.save(bookTagMapper.toEntity(bookTagDto));
  }

  /**
   * Get tags from a book - if the tag does not exist in database it will be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of tags.
   */
  public Set<BookTag> getTagsFromBook(BookDto bookDto) {

    Set<BookTag> bookTagSet = new HashSet<>();

    for (BookTagDto bookTagDto : bookDto.getBookTags()) {
      Optional<BookTag> bookTag = bookTagRepository.findByValue(bookTagDto.getValue());

      if (bookTag.isPresent()) {
        bookTagSet.add(bookTag.get());
      } else {
        BookTag savedBookTag = bookTagRepository.save(bookTagMapper.toEntity(bookTagDto));
        bookTagSet.add(savedBookTag);
      }
    }
    return bookTagSet;
  }
}
