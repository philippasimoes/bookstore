package com.bookstore.catalog_service.service;

import com.bookstore.catalog_service.model.dto.BookDto;
import com.bookstore.catalog_service.model.entity.Publisher;
import com.bookstore.catalog_service.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PublisherService {

  @Autowired PublisherRepository publisherRepository;

  public Publisher getPublisherFromBook(BookDto bookDto) {
    Optional<Publisher> publisher =
        publisherRepository.findByName(bookDto.getPublisher().getName());

    if (publisher.isPresent()) {
      return publisher.get();
    } else {
      Publisher newPublisher = new Publisher();
      newPublisher.setName(bookDto.getPublisher().getName());
      newPublisher.setEmail(bookDto.getPublisher().getEmail());
      newPublisher.setPhoneNumber(bookDto.getPublisher().getPhoneNumber());
      return publisherRepository.save(newPublisher);
    }
  }
}
