package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchCriteria {
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy = "id";
    private String sortDir = "asc";
    private int pageNo = 0;
    private int pageSize = 10;
}