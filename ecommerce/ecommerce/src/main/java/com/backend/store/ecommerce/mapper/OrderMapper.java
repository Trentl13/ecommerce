package com.backend.store.ecommerce.mapper;

import com.backend.store.ecommerce.api.model.DTOs.AddressDTO;
import com.backend.store.ecommerce.api.model.DTOs.OrderDTO;
import com.backend.store.ecommerce.api.model.DTOs.OrderItemDTO;
import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.WebOrder;
import com.backend.store.ecommerce.model.WebOrderQuantities;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderDTO toDTO(WebOrder order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());

        if (order.getAddress() != null) {
            dto.setShippingAddress(toAddressDTO(order.getAddress()));
        }

        List<OrderItemDTO> items = order.getQuantities().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    private AddressDTO toAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAdressLine1());
        dto.setAddressLine2(address.getAdressLine2());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        return dto;
    }

    private OrderItemDTO toOrderItemDTO(WebOrderQuantities quantity) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(quantity.getProduct().getId());
        dto.setProductName(quantity.getProduct().getName());
        dto.setQuantity(quantity.getQuantity());
        dto.setPrice(quantity.getProduct().getPrice());
        dto.setTotal(quantity.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(quantity.getQuantity())));
        return dto;
    }
}
