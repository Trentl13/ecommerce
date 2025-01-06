package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryDetailDTO extends CategoryDTO {
    private Long productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
