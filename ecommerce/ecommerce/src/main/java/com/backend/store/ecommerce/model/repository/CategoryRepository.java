package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CategoryRepository extends ListCrudRepository<Category, Long>,
        PagingAndSortingRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}