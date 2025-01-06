package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.enums.OrderStatus;
import org.hibernate.query.Page;
import org.springframework.data.repository.ListCrudRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);

    List<WebOrder> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    List<WebOrder> findByStatus(OrderStatus status);
}
