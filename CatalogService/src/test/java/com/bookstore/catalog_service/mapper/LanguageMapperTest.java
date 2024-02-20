package com.bookstore.catalog_service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bookstore.catalog_service.dto.LanguageDto;
import com.bookstore.catalog_service.model.Language;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {LanguageMapperImpl.class})
public class LanguageMapperTest {
    
    @Autowired LanguageMapper languageMapper;

  Language language = new Language(1, "PT", new ArrayList<>());

    LanguageDto languageDto = new LanguageDto(2, "ENG", new ArrayList<>());

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
