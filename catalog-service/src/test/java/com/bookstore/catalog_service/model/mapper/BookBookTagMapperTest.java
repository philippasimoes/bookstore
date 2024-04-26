package com.bookstore.catalog_service.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {BookTagMapperImpl.class})
@ActiveProfiles(value = "test")
class BookBookTagMapperTest {

  @Autowired BookTagMapper bookTagMapper;

  BookTag bookTag = new BookTag( "tag_1", new HashSet<>());

  BookTagDto bookTagDto = new BookTagDto(2, "tag_1");

  @Test
  void testTagToTagDto() {

    BookTagDto bookTagDto_2 = bookTagMapper.toDto(bookTag);

    assertEquals(bookTag.getId(), bookTagDto_2.getId());
    assertEquals(bookTag.getValue(), bookTagDto_2.getValue());
  }

  @Test
  void testTagDtoToTag() {

    BookTag bookTag2 = bookTagMapper.toEntity(bookTagDto);

    assertEquals(bookTagDto.getId(), bookTag2.getId());
    assertEquals(bookTagDto.getValue(), bookTag2.getValue());
  }
}
