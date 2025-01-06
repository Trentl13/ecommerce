package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductImage;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends ListCrudRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);

    Optional<ProductImage> findByProductAndIsPrimaryTrue(Product product);
}