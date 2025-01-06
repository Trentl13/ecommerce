package com.backend.store.ecommerce.api.model;

import com.backend.store.ecommerce.model.LocalUser;
import lombok.Data;

@Data
public class AddressBody {

    private Long userId;
    private String adressLine1;
    private String adressLine2;
    private String city;
    private String country;
    private LocalUser user;


}
