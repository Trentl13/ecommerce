package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.ShoppingCart;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CartRepository extends ListCrudRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(LocalUser user);
}