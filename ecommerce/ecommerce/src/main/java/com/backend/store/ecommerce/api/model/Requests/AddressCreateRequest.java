package com.backend.store.ecommerce.api.model.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressCreateRequest {
    @NotBlank(message = "Address line 1 is required")
    @Size(min = 5, max = 100, message = "Address line 1 must be between 5 and 100 characters")
    private String addressLine1;

    @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 56, message = "Country must be between 2 and 56 characters")
    private String country;
}