package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductReview;
import com.backend.store.ecommerce.model.LocalUser;
import java.util.List;

public interface IProductReviewService {
    ProductReview addReview(Product product, LocalUser user, Integer rating, String comment);
    List<ProductReview> getProductReviews(Product product);
    Double getAverageRating(Product product);
    ProductReview updateReview(Long reviewId, Integer rating, String comment);
    void deleteReview(Long reviewId);
    ProductReview getUserReview(Product product, LocalUser user);
}