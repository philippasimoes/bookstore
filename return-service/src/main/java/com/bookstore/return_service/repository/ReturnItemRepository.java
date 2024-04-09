package com.bookstore.return_service.repository;

import com.bookstore.return_service.model.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, Integer> {}
