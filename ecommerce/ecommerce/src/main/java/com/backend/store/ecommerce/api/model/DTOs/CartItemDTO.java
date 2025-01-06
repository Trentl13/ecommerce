package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;

    public void calculateTotalPrice() {
        this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}