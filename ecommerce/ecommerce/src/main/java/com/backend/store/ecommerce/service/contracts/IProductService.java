package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.api.model.DTOs.PagedResponse;
import com.backend.store.ecommerce.api.model.DTOs.ProductDTO;
import com.backend.store.ecommerce.api.model.DTOs.ProductSearchCriteria;
import com.backend.store.ecommerce.api.model.Requests.ProductRequest;
import com.backend.store.ecommerce.model.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IProductService {
    List<Product> getProduct();
    Product getProductById(Long id);
    Product createProduct(ProductRequest request);
    Product updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    List<Product> searchProducts(String keyword);
    PagedResponse<ProductDTO> searchProducts(ProductSearchCriteria criteria);
}