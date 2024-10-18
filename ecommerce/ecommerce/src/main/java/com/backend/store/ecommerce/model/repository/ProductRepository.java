package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product,Long> {
}
