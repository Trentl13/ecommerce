package com.backend.store.ecommerce.model;


import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Version
    private Integer version;
}