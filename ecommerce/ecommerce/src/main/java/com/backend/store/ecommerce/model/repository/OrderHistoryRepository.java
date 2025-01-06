package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.OrderHistory;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderHistoryRepository extends ListCrudRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderIdOrderByTimestampDesc(Long orderId);
}