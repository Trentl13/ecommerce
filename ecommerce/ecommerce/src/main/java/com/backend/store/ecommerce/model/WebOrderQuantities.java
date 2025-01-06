package com.backend.store.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "web_order_quantities")
public class WebOrderQuantities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(name = "quantity",nullable = false)
    private Integer quantity;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id",nullable = false)
    private WebOrder order;


}
