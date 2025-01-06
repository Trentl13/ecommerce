package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
public interface ProductRepository extends ListCrudRepository<Product,Long> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findAll(Pageable pageable);

}
