package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String userName;
}