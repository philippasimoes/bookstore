package com.bookstore.catalog_service.model.mapper;

import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface LanguageMapper {

    LanguageDto languageToLanguageDto(Language language);

    Language languageDtoToLanguage(LanguageDto languageDto);

    List<LanguageDto> languageLisToLanguageDtoList(List<Language> languageList);

    List<Language> languageDtoLisToLanguageList(List<LanguageDto> languageDtoList);

}
