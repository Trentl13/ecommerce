package com.backend.store.ecommerce.mapper;

import com.backend.store.ecommerce.api.model.DTOs.ProductDTO;
import com.backend.store.ecommerce.api.model.Requests.ProductRequest;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    @Autowired
    private CategoryMapper categoryMapper;

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());

        product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .ifPresent(image -> dto.setImageUrl(image.getImageUrl()));

        if (!product.getCategories().isEmpty()) {
            dto.setCategoryId(product.getCategories().iterator().next().getId());
        }

        return dto;
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        updateEntityFromRequest(product, request);
        return product;
    }

    public void updateEntityFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setShortDesc(request.getDescription().substring(0, Math.min(request.getDescription().length(), 100)));
    }
}
