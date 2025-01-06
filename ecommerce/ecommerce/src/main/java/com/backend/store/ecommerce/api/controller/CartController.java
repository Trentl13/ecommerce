package com.backend.store.ecommerce.api.controller;

import com.backend.store.ecommerce.api.model.CartResponse;
import com.backend.store.ecommerce.api.model.DTOs.CartItemDTO;
import com.backend.store.ecommerce.exception.InsufficientInventoryException;
import com.backend.store.ecommerce.model.*;
import com.backend.store.ecommerce.service.ProductService;
import com.backend.store.ecommerce.service.contracts.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Controller", description = "Shopping cart management APIs")
@Validated
public class CartController {
    private final ICartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(ICartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @Operation(summary = "Get cart items", description = "Retrieves all items in the user's cart")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart items")
    @GetMapping
    public ResponseEntity<CartResponse> getCartItems(@AuthenticationPrincipal LocalUser user) {
        List<CartItemDTO> items = cartService.getCartItems(user);
        BigDecimal total = cartService.calculateCartTotal(user);

        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotal(total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add item to cart", description = "Adds a product to the user's cart")
    @ApiResponse(responseCode = "200", description = "Successfully added item to cart")
    @ApiResponse(responseCode = "400", description = "Invalid request or insufficient inventory")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PostMapping("/add/{productId}")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal LocalUser user,
            @PathVariable @Positive Long productId,
            @RequestParam @Min(1) @Max(100) Integer quantity) {

        Product product = productService.getProductById(productId);
        if (product.getInventory().getQuantity() < quantity) {
            throw new InsufficientInventoryException("Not enough inventory available");
        }

        cartService.addToCart(user, productId, quantity);

        return getCartItems(user);
    }

    @Operation(summary = "Remove item from cart", description = "Removes a product from the user's cart")
    @ApiResponse(responseCode = "200", description = "Successfully removed item from cart")
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal LocalUser user,
            @PathVariable @Positive Long productId) {
        cartService.removeFromCart(user, productId);
        return getCartItems(user);
    }

    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a product in the cart")
    @ApiResponse(responseCode = "200", description = "Successfully updated item quantity")
    @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient inventory")
    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @AuthenticationPrincipal LocalUser user,
            @PathVariable @Positive Long productId,
            @RequestParam @Min(1) @Max(100) Integer quantity) {

        Product product = productService.getProductById(productId);
        if (product.getInventory().getQuantity() < quantity) {
            throw new InsufficientInventoryException("Not enough inventory available");
        }

        cartService.updateCartItemQuantity(user, productId, quantity);
        return getCartItems(user);
    }

    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart")
    @ApiResponse(responseCode = "200", description = "Successfully cleared cart")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal LocalUser user) {
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}