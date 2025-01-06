package com.backend.store.ecommerce.api.model;

import lombok.*;

@Data
public class LoginResponse {
    private String jwt;

    private boolean success;

    private String failureReason;


}
