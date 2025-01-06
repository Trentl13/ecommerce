package com.backend.store.ecommerce.api.model;


import lombok.*;

@Data
public class AddressUpdateBody {

    private Long id;
    private String adressLine1;
    private String adressLine2;
    private String city;
    private String country;


}

