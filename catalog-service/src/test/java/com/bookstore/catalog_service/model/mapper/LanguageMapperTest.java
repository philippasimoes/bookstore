package com.bookstore.catalog_service.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {LanguageMapperImpl.class})
@ActiveProfiles(value = "test")
public class LanguageMapperTest {

  @Autowired LanguageMapper languageMapper;

  Language language = new Language("PT", new HashSet<>());

  LanguageDto languageDto = new LanguageDto(2, "ENG");

  @Test
  public void testLanguageToLanguageDto() {

    LanguageDto languageDto1 = languageMapper.toEntity(language);

    assertEquals(language.getId(), languageDto1.getId());
    assertEquals(language.getCode(), languageDto1.getCode());
  }

  @Test
  public void testTagDtoToTag() {

    Language language1 = languageMapper.toDto(languageDto);

    assertEquals(languageDto.getId(), language1.getId());
    assertEquals(languageDto.getCode(), language1.getCode());
  }
}
