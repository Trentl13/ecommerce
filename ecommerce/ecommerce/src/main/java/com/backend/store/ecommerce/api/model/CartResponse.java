package com.backend.store.ecommerce.api.model;

import com.backend.store.ecommerce.api.model.DTOs.CartItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private List<CartItemDTO> items;
    private BigDecimal total;
    private int itemCount;

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}