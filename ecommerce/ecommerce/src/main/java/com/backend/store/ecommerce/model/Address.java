package com.backend.store.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "adress")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "adress_line_1",nullable = false)
    private String adressLine1;
    @Column(name = "adress_line_2")
    private String adressLine2;
    @Column(name = "city",nullable = false)
    private String city;
    @Column(name = "country",nullable = false,length = 70)//най-дългото име на държава е 56
    private String country;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private LocalUser user;

    public Address(String adressLine1, String adressLine2, String city, String country) {
        this.adressLine1 = adressLine1;
        this.adressLine2 = adressLine2;
        this.city = city;
        this.country = country;
    }

    public Address(String adressLine1, String adressLine2, String city, String country, LocalUser user) {
        this.adressLine1 = adressLine1;
        this.adressLine2 = adressLine2;
        this.city = city;
        this.country = country;
        this.user = user;
    }


}
