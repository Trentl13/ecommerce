package com.backend.store.ecommerce.api.controller.product;

import com.backend.store.ecommerce.api.model.DTOs.*;
import com.backend.store.ecommerce.api.model.Requests.ProductImageRequest;
import com.backend.store.ecommerce.api.model.Requests.ProductRequest;
import com.backend.store.ecommerce.api.model.Requests.ProductReviewRequest;
import com.backend.store.ecommerce.mapper.ProductMapper;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.Product;
import com.backend.store.ecommerce.model.ProductImage;
import com.backend.store.ecommerce.model.ProductReview;
import com.backend.store.ecommerce.service.ProductImageService;
import com.backend.store.ecommerce.service.ProductReviewService;
import com.backend.store.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Product Controller", description = "Product management APIs")
public class ProductController {


    private final ProductService productService;
    private final ProductMapper productMapper;

    private final ProductReviewService productReviewService;

    private final ProductImageService productImageService;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper, ProductReviewService productReviewService, ProductImageService productImageService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productReviewService = productReviewService;
        this.productImageService = productImageService;
    }

    @Operation(summary = "Get all products",
            description = "Retrieves a list of all available products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        List<Product> products = productService.getProduct();
        List<ProductDTO> dtos = products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get product by ID",
            description = "Retrieves a specific product by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toDTO(product));
    }

    @Operation(summary = "Create new product",
            description = "Creates a new product with the provided details")
    @ApiResponse(responseCode = "201", description = "Product successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(productMapper.toDTO(created));
    }

    @Operation(summary = "Update product",
            description = "Updates an existing product with the provided details")
    @ApiResponse(responseCode = "200", description = "Product successfully updated")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductRequest request) {
        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(productMapper.toDTO(updated));
    }

    @Operation(summary = "Delete product",
            description = "Deletes a product by its ID")
    @ApiResponse(responseCode = "204", description = "Product successfully deleted")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products",
            description = "Search products with pagination and filtering options")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductDTO>> searchProducts(
            @Parameter(description = "Search criteria")
            @Valid @ModelAttribute ProductSearchCriteria criteria) {
        return ResponseEntity.ok(productService.searchProducts(criteria));
    }

    @Operation(summary = "Quick search products",
            description = "Quick search products by keyword")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping("/quick-search")
    public ResponseEntity<List<ProductDTO>> quickSearchProducts(
            @Parameter(description = "Search keyword")
            @RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductDTO> dtos = products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Add product image",
            description = "Adds a new image to a product")
    @ApiResponse(responseCode = "200", description = "Image successfully added")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImageDTO> addProductImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductImageRequest request) {
        Product product = productService.getProductById(id);
        ProductImage image = productImageService.addImage(
                product, request.getImageUrl(), request.isPrimary());
        return ResponseEntity.ok(mapToImageDTO(image));
    }

    @Operation(summary = "Delete product image",
            description = "Deletes an image from a product")
    @ApiResponse(responseCode = "204", description = "Image successfully deleted")
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long productId,
            @Parameter(description = "Image ID", required = true)
            @PathVariable @Positive Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Set primary image",
            description = "Sets an image as the primary image for a product")
    @ApiResponse(responseCode = "200", description = "Primary image successfully set")
    @PutMapping("/{productId}/images/{imageId}/primary")
    public ResponseEntity<Void> setPrimaryImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long productId,
            @Parameter(description = "Image ID", required = true)
            @PathVariable @Positive Long imageId) {
        productImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add product review",
            description = "Adds a new review to a product")
    @ApiResponse(responseCode = "201", description = "Review successfully added")
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ProductReviewDTO> addProductReview(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductReviewRequest request,
            @AuthenticationPrincipal LocalUser user) {
        Product product = productService.getProductById(id);
        ProductReview review = productReviewService.addReview(
                product, user, request.getRating(), request.getComment());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToReviewDTO(review));
    }

    @Operation(summary = "Get product reviews",
            description = "Gets all reviews for a product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ProductReviewDTO>> getProductReviews(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long id) {
        Product product = productService.getProductById(id);
        List<ProductReview> reviews = productReviewService.getProductReviews(product);
        List<ProductReviewDTO> dtos = reviews.stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private ProductImageDTO mapToImageDTO(ProductImage image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setPrimary(image.isPrimary());
        return dto;
    }

    private ProductReviewDTO mapToReviewDTO(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUserName(review.getUser().getUsername());
        return dto;
    }
}