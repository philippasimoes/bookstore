package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.BookTagDto;
import com.bookstore.catalog_service.model.entity.BookTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BookTagMapperImpl.class})
public class BookBookTagMapperTest {

  @Autowired
  BookTagMapper bookTagMapper;

  BookTag bookTag = new BookTag(1, "tag_1", new ArrayList<>());

  BookTagDto bookTagDto = new BookTagDto(2, "tag_1");

  @Test
  public void testTagToTagDto() {

    BookTagDto bookTagDto_2 = bookTagMapper.bookTagToBookTagDto(bookTag);

    assertEquals(bookTag.getId(), bookTagDto_2.getId());
    assertEquals(bookTag.getValue(), bookTagDto_2.getValue());
  }

  @Test
  public void testTagDtoToTag() {

    BookTag bookTag2 = bookTagMapper.bookTagDtoToBookTag(bookTagDto);

    assertEquals(bookTagDto.getId(), bookTag2.getId());
    assertEquals(bookTagDto.getValue(), bookTag2.getValue());
  }
}
