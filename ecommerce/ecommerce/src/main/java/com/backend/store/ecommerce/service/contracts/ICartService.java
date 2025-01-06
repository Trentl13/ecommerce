package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.api.model.DTOs.CartItemDTO;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;

public interface ICartService {

    List<CartItemDTO> getCartItems(LocalUser user);

    void addToCart(LocalUser user, Long productId, Integer quantity);

    void removeFromCart(LocalUser user, Long productId);

    void updateCartItemQuantity(LocalUser user, Long productId, Integer quantity);

    void clearCart(LocalUser user);

    ShoppingCart getCart(LocalUser user);

    BigDecimal calculateCartTotal(LocalUser user);
}
