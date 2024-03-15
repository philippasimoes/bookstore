package com.bookstore.return_service.repository;

import com.bookstore.return_service.model.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRepository extends JpaRepository<Return, Integer> {}
