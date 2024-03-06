package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {LanguageMapperImpl.class})
public class LanguageMapperTest {
    
    @Autowired
    LanguageMapper languageMapper;

  Language language = new Language(1, "PT", new ArrayList<>());

    LanguageDto languageDto = new LanguageDto(2, "ENG");

    @Test
    public void testLanguageToLanguageDto() {

        LanguageDto languageDto1 = languageMapper.languageToLanguageDto(language);

        assertEquals(language.getId(), languageDto1.getId());
        assertEquals(language.getCode(), languageDto1.getCode());
    }

    @Test
    public void testTagDtoToTag() {

        Language language1 = languageMapper.languageDtoToLanguage(languageDto);

        assertEquals(languageDto.getId(), language1.getId());
        assertEquals(languageDto.getCode(), language1.getCode());
    }
}
