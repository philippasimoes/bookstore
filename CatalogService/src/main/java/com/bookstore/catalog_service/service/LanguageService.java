package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.mapper.LanguageMapper;
import com.bookstore.catalog_service.repository.LanguageRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

  private static final Logger LOGGER = LogManager.getLogger(LanguageService.class);
  @Autowired LanguageRepository languageRepository;

  @Autowired LanguageMapper languageMapper;

  public List<LanguageDto> getAllLanguages() {
    return languageMapper.languageListToLanguageDtoList(languageRepository.findAll());
  }

  @Transactional
  public Language addNewLanguage(LanguageDto languageDto) {
    if (languageRepository.existsByCode(languageDto.getCode())) {
      LOGGER.error(
          String.format("Language with code %s is already in database.", languageDto.getCode()));
      throw new DuplicatedResourceException("Language already exists.");
    } else return languageRepository.save(languageMapper.languageDtoToLanguage(languageDto));
  }
}
