package com.bookstore.stock_service.service;

import com.bookstore.stock_service.exception.InsufficientStockException;
import com.bookstore.stock_service.exception.ResourceNotFoundException;
import com.bookstore.stock_service.model.entity.Stock;
import com.bookstore.stock_service.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    public int addStock(int bookId, int newStock) {

        if (stockIsPresent(bookId)) {
            Stock stock = stockRepository.findByBookId(bookId).get();
            stock.setAvailableStock(stock.getAvailableStock() + newStock);
            Stock updated = stockRepository.save(stock);
            return updated.getAvailableStock();
        } else {
            Stock newStockEntry = new Stock();
            newStockEntry.setAvailableStock(newStock);
            newStockEntry.setBookId(bookId);
            Stock updated = stockRepository.save(newStockEntry);
            return updated.getAvailableStock();
        }
    }

    public int decreaseStock(int bookId, int newStock) {

        if (stockIsPresent(bookId)) {
            Stock stock = stockRepository.findByBookId(bookId).get();
            if (newStock <= stock.getAvailableStock()) {
                stock.setAvailableStock(stock.getAvailableStock() - newStock);
                Stock updatedStock = stockRepository.save(stock);
                return updatedStock.getAvailableStock();
            } else throw new InsufficientStockException();
        } else throw new ResourceNotFoundException();

    }

    public boolean stockIsPresent(int bookId) {

        return stockRepository.findByBookId(bookId).isPresent();
    }

    public Stock getStockByBookId(int bookId) {

        return stockRepository.findByBookId(bookId).orElseThrow(ResourceNotFoundException::new);
    }

}
