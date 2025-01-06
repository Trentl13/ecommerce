package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.OrderHistory;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.enums.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderService {
    List<WebOrder> getOrders(LocalUser user);

    WebOrder createOrder(LocalUser user, Long addressId);

    WebOrder createOrder(LocalUser user);

    WebOrder getOrderById(Long id);

    @Transactional
    void updateOrderStatus(Long id, OrderStatus status, String comment);

    void cancelOrder(Long id);

    void updateOrderStatus(Long id, OrderStatus status);

    List<OrderHistory> getOrderHistory(Long orderId);

    List<WebOrder> getOrdersByStatus(OrderStatus status);

    List<WebOrder> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
}