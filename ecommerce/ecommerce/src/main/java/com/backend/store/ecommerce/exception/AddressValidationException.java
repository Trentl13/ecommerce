package com.backend.store.ecommerce.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AddressValidationException extends RuntimeException {
    private final List<String> errors;

    public AddressValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

}