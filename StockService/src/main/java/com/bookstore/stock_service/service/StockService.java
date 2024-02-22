package com.bookstore.stock_service.service;

import com.bookstore.stock_service.dto.StockDto;
import com.bookstore.stock_service.exception.ResourceNotFoundException;
import com.bookstore.stock_service.model.Stock;
import com.bookstore.stock_service.repository.StockRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;
    public String addStock(int bookId, int newStock) {

        if (stockIsPresent(bookId)) {
            Stock stock = stockRepository.findByBookId(bookId).get();
            stock.setAvailableStock(stock.getAvailableStock() + newStock);
            stockRepository.save(stock);
        } else {
            Stock newStockEntry = new Stock();
            newStockEntry.setAvailableStock(newStock);
            newStockEntry.setBookId(bookId);
            stockRepository.save(newStockEntry);
        }
        return "Stock updated";
    }

    public boolean stockIsPresent(int bookId) {
        return stockRepository.findByBookId(bookId).isPresent();
    }

    public Stock getStockByBookId(int bookId) {
        return stockRepository.findByBookId(bookId).orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    }
}
