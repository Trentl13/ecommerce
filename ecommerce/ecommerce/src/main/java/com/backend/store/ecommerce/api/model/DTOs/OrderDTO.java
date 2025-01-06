package com.backend.store.ecommerce.api.model.DTOs;

import com.backend.store.ecommerce.model.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private AddressDTO shippingAddress;
    private List<OrderItemDTO> items;
    private BigDecimal total;

    public BigDecimal getTotal() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(OrderItemDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}