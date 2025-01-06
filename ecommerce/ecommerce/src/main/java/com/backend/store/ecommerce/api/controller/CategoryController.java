package com.backend.store.ecommerce.api.controller;

import com.backend.store.ecommerce.api.model.DTOs.*;
import com.backend.store.ecommerce.api.model.Requests.CategoryCreateRequest;
import com.backend.store.ecommerce.api.model.Requests.CategoryUpdateRequest;
import com.backend.store.ecommerce.mapper.CategoryMapper;
import com.backend.store.ecommerce.model.Category;
import com.backend.store.ecommerce.service.ProductService;
import com.backend.store.ecommerce.service.contracts.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "Category management APIs")
@Validated
public class CategoryController {
    private final ICategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ProductService productService;

    @Autowired
    public CategoryController(ICategoryService categoryService,
                              CategoryMapper categoryMapper,
                              ProductService productService) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.productService = productService;
    }

    @Operation(summary = "Get all categories",
            description = "Retrieves all categories with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @GetMapping
    public ResponseEntity<PagedResponse<CategoryDTO>> getAllCategories(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryService.getAllCategories(pageable);

        List<CategoryDTO> content = categoryPage.getContent().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        PagedResponse<CategoryDTO> response = new PagedResponse<>(
                content,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get category by ID",
            description = "Retrieves a specific category by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved category")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailDTO> getCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable @Positive Long id) {
        Category category = categoryService.getCategory(id);
        CategoryDetailDTO dto = categoryMapper.toDetailDTO(category);

        dto.setProductCount((long) category.getProducts().size());

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Create category",
            description = "Creates a new category")
    @ApiResponse(responseCode = "201", description = "Category successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {
        // Convert request to entity
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryService.createCategory(category);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategory.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(categoryMapper.toDTO(savedCategory));
    }

    @Operation(summary = "Update category",
            description = "Updates an existing category")
    @ApiResponse(responseCode = "200", description = "Category successfully updated")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        Category category = categoryService.getCategory(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
    }

    @Operation(summary = "Delete category",
            description = "Deletes a category and removes it from all products")
    @ApiResponse(responseCode = "204", description = "Category successfully deleted")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search categories",
            description = "Searches categories by keyword with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<CategoryDTO>> searchCategories(
            @Parameter(description = "Search keyword")
            @RequestParam String keyword,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.searchCategories(keyword, pageable);

        List<CategoryDTO> content = categoryPage.getContent().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        PagedResponse<CategoryDTO> response = new PagedResponse<>(
                content,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get category products",
            description = "Retrieves all products in a category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping("/{id}/products")
    public ResponseEntity<PagedResponse<ProductDTO>> getCategoryProducts(
            @Parameter(description = "Category ID", required = true)
            @PathVariable @Positive Long id,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Category category = categoryService.getCategory(id);
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setCategoryId(id);
        criteria.setPageNo(page);
        criteria.setPageSize(size);

        return ResponseEntity.ok(productService.searchProducts(criteria));
    }

}