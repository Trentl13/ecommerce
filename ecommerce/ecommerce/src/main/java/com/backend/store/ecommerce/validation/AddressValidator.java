package com.backend.store.ecommerce.validation;

import com.backend.store.ecommerce.exception.AddressValidationException;
import com.backend.store.ecommerce.model.Address;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressValidator {

    public void validateAddress(Address address) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(address.getAdressLine1())) {
            errors.add("Address line 1 is required");
        } else if (address.getAdressLine1().length() > 100) {
            errors.add("Address line 1 cannot exceed 100 characters");
        }

        if (address.getAdressLine2() != null && address.getAdressLine2().length() > 100) {
            errors.add("Address line 2 cannot exceed 100 characters");
        }

        if (StringUtils.isBlank(address.getCity())) {
            errors.add("City is required");
        } else if (address.getCity().length() > 50) {
            errors.add("City cannot exceed 50 characters");
        }

        if (StringUtils.isBlank(address.getCountry())) {
            errors.add("Country is required");
        } else if (address.getCountry().length() > 56) {
            errors.add("Country cannot exceed 56 characters");
        }

        if (!errors.isEmpty()) {
            throw new AddressValidationException("Address validation failed", errors);
        }
    }
}