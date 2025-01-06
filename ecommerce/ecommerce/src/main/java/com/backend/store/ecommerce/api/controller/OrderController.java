package com.backend.store.ecommerce.api.controller;

import com.backend.store.ecommerce.api.model.DTOs.CartItemDTO;
import com.backend.store.ecommerce.api.model.DTOs.OrderDTO;
import com.backend.store.ecommerce.exception.EmptyCartException;
import com.backend.store.ecommerce.exception.InvalidAddressException;
import com.backend.store.ecommerce.exception.InvalidOrderStateException;
import com.backend.store.ecommerce.exception.InvalidStatusTransitionException;
import com.backend.store.ecommerce.mapper.OrderMapper;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.OrderHistory;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.enums.OrderStatus;
import com.backend.store.ecommerce.service.contracts.IAddressService;
import com.backend.store.ecommerce.service.contracts.ICartService;
import com.backend.store.ecommerce.service.contracts.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller", description = "Order management APIs")
@Validated
public class OrderController {
    private final IOrderService orderService;
    private final ICartService cartService;
    private final OrderMapper orderMapper;
    private final IAddressService addressService;

    @Autowired
    public OrderController(IOrderService orderService,
                           ICartService cartService,
                           OrderMapper orderMapper,
                           IAddressService addressService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
        this.addressService = addressService;
    }

    @Operation(summary = "Get user orders", description = "Retrieves all orders for the current user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(@AuthenticationPrincipal LocalUser user) {
        List<WebOrder> orders = orderService.getOrders(user);
        return ResponseEntity.ok(orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Create order", description = "Creates a new order from the user's cart")
    @ApiResponse(responseCode = "201", description = "Order successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request or empty cart")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @AuthenticationPrincipal LocalUser user,
            @RequestParam @Positive Long addressId) {

        // Verify address belongs to user
        if (!addressService.validateAddressBelongsToUser(addressId, user)) {
            throw new InvalidAddressException("Address does not belong to user");
        }

        // Verify cart is not empty
        List<CartItemDTO> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order with empty cart");
        }

        // Create order
        WebOrder order = orderService.createOrder(user, addressId);

        // Clear cart after successful order creation
        cartService.clearCart(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toDTO(order));
    }

    @Operation(summary = "Get order details", description = "Retrieves details of a specific order")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved order")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal LocalUser user) {
        WebOrder order = orderService.getOrderById(id);

        // Verify order belongs to user or user is admin
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to view this order");
        }

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Operation(summary = "Update order status", description = "Updates the status of an order")
    @ApiResponse(responseCode = "200", description = "Order status successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable @Positive Long id,
            @RequestParam @NotNull OrderStatus status,
            @RequestParam(required = false) String comment) {

        WebOrder order = orderService.getOrderById(id);

        // Validate status transition
        validateStatusTransition(order.getStatus(), status);

        orderService.updateOrderStatus(id, status, comment);
        return ResponseEntity.ok(orderMapper.toDTO(orderService.getOrderById(id)));
    }

    @Operation(summary = "Cancel order", description = "Cancels an order if it's in a cancellable state")
    @ApiResponse(responseCode = "200", description = "Order successfully cancelled")
    @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal LocalUser user) {

        WebOrder order = orderService.getOrderById(id);

        // Verify order belongs to user or user is admin
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to cancel this order");
        }

        // Verify order can be cancelled
        if (!canCancel(order.getStatus())) {
            throw new InvalidOrderStateException("Order cannot be cancelled in current state");
        }

        orderService.cancelOrder(id);
        return ResponseEntity.ok(orderMapper.toDTO(orderService.getOrderById(id)));
    }

    @Operation(summary = "Get order history", description = "Retrieves the status history of an order")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved order history")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<OrderHistory>> getOrderHistory(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal LocalUser user) {

        WebOrder order = orderService.getOrderById(id);

        // Verify order belongs to user or user is admin
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to view this order's history");
        }

        return ResponseEntity.ok(orderService.getOrderHistory(id));
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidStatusTransitionException("Invalid status transition from PENDING");
                }
                break;
            case PROCESSING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidStatusTransitionException("Invalid status transition from PROCESSING");
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new InvalidStatusTransitionException("Invalid status transition from SHIPPED");
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new InvalidStatusTransitionException("Cannot change status of DELIVERED or CANCELLED orders");
        }
    }

    private boolean canCancel(OrderStatus status) {
        return status == OrderStatus.PENDING || status == OrderStatus.PROCESSING;
    }
}