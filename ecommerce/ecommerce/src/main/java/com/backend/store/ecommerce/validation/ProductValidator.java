package com.backend.store.ecommerce.validation;

import com.backend.store.ecommerce.api.model.Requests.ProductRequest;
import com.backend.store.ecommerce.exception.ProductValidationException;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {
    public void validateProduct(ProductRequest request) {
        if (request.getPrice() != null && request.getPrice().scale() > 2) {
            throw new ProductValidationException("Price cannot have more than 2 decimal places");
        }

        if (request.getName() != null && request.getName().length() > 255) {
            throw new ProductValidationException("Product name cannot exceed 255 characters");
        }

        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            throw new ProductValidationException("Description cannot exceed 1000 characters");
        }
    }
}