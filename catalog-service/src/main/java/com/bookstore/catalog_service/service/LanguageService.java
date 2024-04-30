package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.exception.DuplicatedResourceException;
import com.bookstore.catalog_service.exception.ResourceNotFoundException;
import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.dto.LanguageDto;
import com.bookstore.catalog_service.model.entity.Language;
import com.bookstore.catalog_service.model.mapper.LanguageMapper;
import com.bookstore.catalog_service.repository.LanguageRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Language service class.
 *
 * @author Filipa Simões
 */
@Service
public class LanguageService {

  private static final Logger LOGGER = LogManager.getLogger(LanguageService.class);

  @Autowired LanguageRepository languageRepository;
  @Autowired LanguageMapper languageMapper;

  public Language getLanguageById(int id) {
    return languageRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Language not found."));
  }

  public Set<LanguageDto> getAllLanguages() {
    return languageMapper.toDtoSet(new HashSet<>(languageRepository.findAll()));
  }

  public Language addNewLanguage(LanguageDto languageDto) {
    if (languageRepository.existsByCode(languageDto.getCode())) {

      LOGGER.log(
          Level.ERROR, "Language with code {} is already in database.", languageDto.getCode());

      throw new DuplicatedResourceException("Language already exists.");
    } else return languageRepository.save(languageMapper.toDto(languageDto));
  }

  /**
   * Get languages from a book - if the language does not exist in database it will be created.
   *
   * @param bookDto the book (data transfer object) sent in request.
   * @return a set of languages.
   */
  public Set<Language> getLanguageFromBook(BookDto bookDto) {

    Set<Language> languageSet = new HashSet<>();

    for (LanguageDto languageDto : bookDto.getLanguages()) {
      Optional<Language> language = languageRepository.findByCode(languageDto.getCode());

      if (language.isPresent()) {
        languageSet.add(language.get());
      } else {
        Language savedLanguage = languageRepository.save(languageMapper.toDto(languageDto));
        languageSet.add(savedLanguage);
      }
    }
    return languageSet;
  }
}
