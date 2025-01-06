package com.backend.store.ecommerce.mapper;

import com.backend.store.ecommerce.api.model.DTOs.CategoryDTO;
import com.backend.store.ecommerce.api.model.DTOs.CategoryDetailDTO;
import com.backend.store.ecommerce.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }


    public CategoryDetailDTO toDetailDTO(Category category) {
        CategoryDetailDTO dto = new CategoryDetailDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        return dto;
    }
}