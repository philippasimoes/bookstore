package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface LanguageMapper {

    @Mapping(target="books", ignore = true)
    LanguageDto languageToLanguageDto(Language language);

    @Mapping(target="books", ignore = true)
    Language languageDtoToLanguage(LanguageDto languageDto);

    List<LanguageDto> languageLisToLanguageDtoList(List<Language> languageList);

    List<Language> languageDtoLisToLanguageList(List<LanguageDto> languageDtoList);

}
