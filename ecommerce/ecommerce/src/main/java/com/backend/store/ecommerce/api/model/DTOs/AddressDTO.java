package com.backend.store.ecommerce.api.model.DTOs;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
}