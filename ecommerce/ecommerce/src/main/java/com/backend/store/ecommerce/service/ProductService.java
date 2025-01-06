package com.backend.store.ecommerce.service;


import com.backend.store.ecommerce.api.model.Requests.ProductRequest;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.repository.ProductRepository;
import com.backend.store.ecommerce.service.contracts.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;

import com.backend.store.ecommerce.api.model.DTOs.*;
import com.backend.store.ecommerce.exception.*;
import com.backend.store.ecommerce.validation.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    @Override
    public List<Product> getProduct() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


    @Override
    @Transactional
    public Product createProduct(ProductRequest request) {
        productValidator.validateProduct(request);

        Product product = new Product();
        updateProductFromRequest(product, request);
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        productValidator.validateProduct(request);

        updateProductFromRequest(product, request);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return List.of();
    }

    @Override
    public PagedResponse<ProductDTO> searchProducts(ProductSearchCriteria criteria) {
        Sort sort = Sort.by(
                criteria.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                criteria.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                criteria.getPageNo(),
                criteria.getPageSize(),
                sort
        );

        Page<Product> productPage;

        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(
                    criteria.getKeyword(),
                    pageable
            );
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductDTO> content = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        //other fields??
        return dto;
    }
}
