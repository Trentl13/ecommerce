package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductReview;
import org.springframework.data.repository.ListCrudRepository;

import java.util.*;

public interface ProductReviewRepository extends ListCrudRepository<ProductReview, Long> {
    List<ProductReview> findByProduct(Product product);
    Optional<ProductReview> findByProductAndUser(Product product, LocalUser user);
    Double findAverageRatingByProduct(Product product);
}