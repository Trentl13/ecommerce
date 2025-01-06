package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.enums.OrderStatus;
import com.backend.store.ecommerce.model.repository.OrderHistoryRepository;
import com.backend.store.ecommerce.model.repository.WebOrderRepository;
import com.backend.store.ecommerce.service.contracts.IAddressService;
import com.backend.store.ecommerce.service.contracts.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

import com.backend.store.ecommerce.exception.EntityNotFoundException;
import com.backend.store.ecommerce.model.*;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private WebOrderRepository webOrderRepository;
    @Autowired
    private IAddressService adressService;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<WebOrder> getOrders(LocalUser user) {
        return webOrderRepository.findByUser(user);
    }

    @Override
    public WebOrder createOrder(LocalUser user) {
        return createOrder(user, null);
    }

    @Override
    @Transactional
    public WebOrder createOrder(LocalUser user, Long addressId) {
        WebOrder order = new WebOrder();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        order.setAddress(addressId != null ? adressService.getAddressById(addressId) : null);
        return webOrderRepository.save(order);
    }

    @Override
    public WebOrder getOrderById(Long id) {
        return webOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id));
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, OrderStatus status) {
        WebOrder order = getOrderById(id);
        order.setStatus(status);
        webOrderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateOrderStatus(Long id, OrderStatus status, String comment) {
        WebOrder order = getOrderById(id);
        order.setStatus(status);
        webOrderRepository.save(order);

        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setTimestamp(LocalDateTime.now());
        history.setComment(comment);
        orderHistoryRepository.save(history);
    }


    @Override
    @Transactional
    public void cancelOrder(Long id) {
        WebOrder order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED);
        webOrderRepository.save(order);
    }

    @Override
    public List<OrderHistory> getOrderHistory(Long orderId) {
        return orderHistoryRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }

    @Override
    public List<WebOrder> getOrdersByStatus(OrderStatus status) {
        return webOrderRepository.findByStatus(status);
    }


    @Override
    public List<WebOrder> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return webOrderRepository.findByOrderDateBetween(start, end);
    }


}