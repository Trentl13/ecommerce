package com.backend.store.ecommerce.exception;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(String message) {
        super(message);
    }

}
