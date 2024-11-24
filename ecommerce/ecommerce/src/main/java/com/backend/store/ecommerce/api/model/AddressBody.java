package com.backend.store.ecommerce.api.model;

import com.backend.store.ecommerce.model.LocalUser;

public class AddressBody {

    private Long id;
    private String adressLine1;
    private String adressLine2;
    private String city;
    private String country;
    private LocalUser user;

    public Long getId() {

        return id;
    }
    public String getAdressLine1() {
        return adressLine1;
    }

    public void setAdressLine1(String adressLine1) {
        this.adressLine1 = adressLine1;
    }

    public String getAdressLine2() {
        return adressLine2;
    }

    public void setAdressLine2(String adressLine2) {
        this.adressLine2 = adressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalUser getUser() {
        return user;
    }




}
