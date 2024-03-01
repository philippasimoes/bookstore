package com.bookstore.stock_service.service;

import com.bookstore.stock_service.exception.InsufficientStockException;
import com.bookstore.stock_service.exception.ResourceNotFoundException;
import com.bookstore.stock_service.model.entity.Stock;
import com.bookstore.stock_service.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StockService {
    private static final Logger LOGGER = LogManager.getLogger(StockService.class);
    @Autowired
    StockRepository stockRepository;

    public int addStock(int bookId, int newStock) {

        if (stockIsPresent(bookId)) {
            Stock stock = stockRepository.findByBookId(bookId).get();
            stock.setAvailableStock(stock.getAvailableStock() + newStock);
            Stock updated = stockRepository.save(stock);
            LOGGER.info(String.format("Stock updated. Book with id %s have %s units", bookId, newStock));
            return updated.getAvailableStock();
        } else {
            Stock newStockEntry = new Stock();
            newStockEntry.setAvailableStock(newStock);
            newStockEntry.setBookId(bookId);
            Stock updated = stockRepository.save(newStockEntry);
            LOGGER.info(String.format("Stock entry created for book with id %s, units available: %s", bookId, newStock));
            return updated.getAvailableStock();
        }
    }

    public int decreaseStock(int bookId, int newStock) {

        if (stockIsPresent(bookId)) {
            Stock stock = stockRepository.findByBookId(bookId).get();
            if (newStock <= stock.getAvailableStock()) {
                stock.setAvailableStock(stock.getAvailableStock() - newStock);
                Stock updatedStock = stockRepository.save(stock);
                LOGGER.info(String.format("Stock updated. Book with id %s have %s units", bookId, updatedStock.getAvailableStock()));
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
