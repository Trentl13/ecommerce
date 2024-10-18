package com.backend.store.ecommerce.service;


import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProduct(){
        return productRepository.findAll();
    }
}
