package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddressDetailDTO extends AddressDTO {
    private Long userId;
    private String username;
    private Integer associatedOrdersCount;
}