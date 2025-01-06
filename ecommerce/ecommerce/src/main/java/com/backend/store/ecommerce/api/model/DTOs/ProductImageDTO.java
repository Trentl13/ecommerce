package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;

@Data
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private boolean primary;
}