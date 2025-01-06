package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.exception.EntityNotFoundException;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductReview;
import com.backend.store.ecommerce.model.repository.ProductReviewRepository;
import com.backend.store.ecommerce.service.contracts.IProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductReviewService implements IProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    @Autowired
    public ProductReviewService(ProductReviewRepository productReviewRepository) {
        this.productReviewRepository = productReviewRepository;
    }

    @Override
    public ProductReview addReview(Product product, LocalUser user, Integer rating, String comment) {
        validateRating(rating);

        productReviewRepository.findByProductAndUser(product, user)
                .ifPresent(existingReview -> {
                    throw new IllegalStateException("User already reviewed this product.");
                });

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return productReviewRepository.save(review);
    }

    @Override
    public List<ProductReview> getProductReviews(Product product) {
        return productReviewRepository.findByProduct(product);
    }

    @Override
    public Double getAverageRating(Product product) {
        return productReviewRepository.findAverageRatingByProduct(product);
    }

    @Override
    public ProductReview updateReview(Long reviewId, Integer rating, String comment) {
        validateRating(rating);

        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);

        return productReviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        productReviewRepository.deleteById(reviewId);
    }

    @Override
    public ProductReview getUserReview(Product product, LocalUser user) {
        return productReviewRepository.findByProductAndUser(product, user)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}