package com.backend.store.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "local_user")
public class LocalUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto-increment на ID-то
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "username",nullable = false,unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password",nullable = false,length = 1000)//1000 защото ще бъде енкриптирана
    private String password;

    @Column(name = "email",nullable = false, unique = true, length = 320)//320 е явно стандарта за дължина на имейли
    private String email;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)//cascade = CascadeType.REMOVE открива всички деца на родител
    // и ги трие АКО нещото тагнато с @OneToMany е изтрито, обаче ако е изтрито нещо с тагнато @ManyToMany трие само връзката, а децата си остават. В случая един user ако се изтрие се изтриват и адресите които е сложил
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")//descending за да пълни по реда в който се инкрементира
    private List<VerificationToken> verificationTokens = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")//descending за да пълни по реда в който се инкрементира
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();


    public boolean isEmailVerified() {return emailVerified;}
}
