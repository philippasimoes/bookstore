package com.bookstore.return_service.repository;

import com.bookstore.return_service.model.dto.enums.RefundType;
import com.bookstore.return_service.model.dto.enums.ReturnStatus;
import com.bookstore.return_service.model.entity.Return;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRepository extends JpaRepository<Return, Integer> {

  List<Return> findByCustomerId(int customerId);

  List<Return> findByOrderId(int orderId);

  List<Return> findByRefundType(RefundType refundType);

  List<Return> findByReturnStatus(ReturnStatus returnStatus);
}
