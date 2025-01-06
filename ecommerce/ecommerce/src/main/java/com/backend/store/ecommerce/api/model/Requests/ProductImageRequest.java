package com.backend.store.ecommerce.api.model.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductImageRequest {
    @NotBlank
    private String imageUrl;
    private boolean primary;
}