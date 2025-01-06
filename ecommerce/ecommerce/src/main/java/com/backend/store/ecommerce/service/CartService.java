
package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.exception.EntityNotFoundException;
import com.backend.store.ecommerce.model.*;
import com.backend.store.ecommerce.model.repository.CartRepository;
import com.backend.store.ecommerce.api.model.DTOs.CartItemDTO;
import com.backend.store.ecommerce.service.contracts.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    public List<CartItemDTO> getCartItems(LocalUser user) {
        return cartRepository.findByUser(user)
                .map(cart -> cart.getItems().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void addToCart(LocalUser user, Long productId, Integer quantity) {
        ShoppingCart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));

        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Override
    public ShoppingCart getCart(LocalUser user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
    }

    @Override
    public BigDecimal calculateCartTotal(LocalUser user) {
        ShoppingCart cart = getCart(user);
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void removeFromCart(LocalUser user, Long productId) {
        ShoppingCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    @Override
    public void updateCartItemQuantity(LocalUser user, Long productId, Integer quantity) {
        ShoppingCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        cartRepository.save(cart);
    }

    @Override
    public void clearCart(LocalUser user) {
        cartRepository.findByUser(user).ifPresent(cartRepository::delete);
    }

    private ShoppingCart createNewCart(LocalUser user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private CartItemDTO convertToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setProductName(item.getProduct().getName());
        dto.setPrice(item.getProduct().getPrice());
        return dto;
    }
}