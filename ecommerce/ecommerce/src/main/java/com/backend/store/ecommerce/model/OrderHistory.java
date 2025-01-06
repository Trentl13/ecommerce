package com.backend.store.ecommerce.model;

import com.backend.store.ecommerce.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
@Data
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private WebOrder order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime timestamp;
    private String comment;
}