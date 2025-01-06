package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductImage;
import java.util.List;

public interface IProductImageService {
    ProductImage addImage(Product product, String imageUrl, boolean isPrimary);
    void deleteImage(Long imageId);
    List<ProductImage> getProductImages(Product product);
    ProductImage getPrimaryImage(Product product);
    void setPrimaryImage(Long imageId);
}