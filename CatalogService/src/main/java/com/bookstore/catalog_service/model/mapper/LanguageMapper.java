package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface LanguageMapper {

    LanguageDto languageToLanguageDto(Language language);

    Language languageDtoToLanguage(LanguageDto languageDto);

    List<LanguageDto> languageListToLanguageDtoList(List<Language> languageList);

    List<Language> languageDtoListToLanguageList(List<LanguageDto> languageDtoList);

}
