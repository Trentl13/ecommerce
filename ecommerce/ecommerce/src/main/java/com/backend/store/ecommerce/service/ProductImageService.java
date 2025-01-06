package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.exception.EntityNotFoundException;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductImage;
import com.backend.store.ecommerce.model.repository.ProductImageRepository;
import com.backend.store.ecommerce.model.repository.ProductRepository;
import com.backend.store.ecommerce.service.contracts.IProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService implements IProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductImageService(ProductImageRepository productImageRepository,
                               ProductRepository productRepository) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }


    @Override
    public ProductImage addImage(Product product, String imageUrl, boolean isPrimary) {
        if (isPrimary) {
            productImageRepository.findByProductAndIsPrimaryTrue(product)
                    .ifPresent(image -> {
                        image.setPrimary(false);
                        productImageRepository.save(image);
                    });
        }

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setPrimary(isPrimary);
        return productImageRepository.save(image);
    }

    @Override
    public void deleteImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    @Override
    public List<ProductImage> getProductImages(Product product) {
        return productImageRepository.findByProduct(product);
    }

    @Override
    public ProductImage getPrimaryImage(Product product) {
        return productImageRepository.findByProductAndIsPrimaryTrue(product)
                .orElseThrow(() -> new EntityNotFoundException("Primary image not found for product"));
    }

    @Override
    public void setPrimaryImage(Long imageId) {
        ProductImage newPrimary = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        Product product = newPrimary.getProduct();

        productImageRepository.findByProductAndIsPrimaryTrue(product)
                .ifPresent(currentPrimary -> {
                    currentPrimary.setPrimary(false);
                    productImageRepository.save(currentPrimary);
                });

        newPrimary.setPrimary(true);
        productImageRepository.save(newPrimary);
    }

}