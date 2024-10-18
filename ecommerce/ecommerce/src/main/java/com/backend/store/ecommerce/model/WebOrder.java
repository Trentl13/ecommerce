package com.backend.store.ecommerce.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "web_order")
public class WebOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_user_id",nullable = false)
    private LocalUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id",nullable = false)
    private Adress adress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<WebOrderQuantities> quantities = new ArrayList<>();

    public List<WebOrderQuantities> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<WebOrderQuantities> quantities) {
        this.quantities = quantities;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public LocalUser getUser() {
        return user;
    }

    public void setUser(LocalUser user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
