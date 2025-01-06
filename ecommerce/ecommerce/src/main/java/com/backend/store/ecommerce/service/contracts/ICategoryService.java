package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    Page<Category> getAllCategories(Pageable pageable);

    Category getCategory(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    Page<Category> searchCategories(String keyword, Pageable pageable);
}