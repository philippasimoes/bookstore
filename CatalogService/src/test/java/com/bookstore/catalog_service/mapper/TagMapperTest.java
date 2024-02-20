package com.bookstore.catalog_service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bookstore.catalog_service.dto.TagDto;
import com.bookstore.catalog_service.model.Tag;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TagMapperImpl.class})
public class TagMapperTest {

  @Autowired TagMapper tagMapper;

  Tag tag = new Tag(1, "tag_1", new ArrayList<>());

  TagDto tagDto = new TagDto(2, "tag_1", new ArrayList<>());

  @Test
  public void testTagToTagDto() {

    TagDto tagDto_2 = tagMapper.tagToTagDto(tag);

    assertEquals(tag.getId(), tagDto_2.getId());
    assertEquals(tag.getValue(), tagDto_2.getValue());
  }

  @Test
  public void testTagDtoToTag() {

    Tag tag2 = tagMapper.tagDtoToTag(tagDto);

    assertEquals(tagDto.getId(), tag2.getId());
    assertEquals(tagDto.getValue(), tag2.getValue());
  }
}