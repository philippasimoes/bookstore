package com.bookstore.stock_service.service;

import com.bookstore.stock_service.dto.StockDto;
import com.bookstore.stock_service.exception.ResourceNotFoundException;
import com.bookstore.stock_service.model.Stock;
import com.bookstore.stock_service.repository.StockRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    public StockDto getAuthorByID(int id) {
        Stock author =
                stockRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        StockDto authorDto = new StockDto();
        BeanUtils.copyProperties(author, authorDto);
        return authorDto;
    }
}
