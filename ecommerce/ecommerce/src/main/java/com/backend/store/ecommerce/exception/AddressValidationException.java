package com.backend.store.ecommerce.exception;

import java.util.List;

public class AddressValidationException extends RuntimeException {
    private final List<String> errors;

    public AddressValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}