package com.backend.store.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

//правим лист от токени, понеже искаме да проверяваме дали сме пратили имейл токен за верификация наскоро при user-a
//и ако не сме да му пратим нов
@Data
@Entity
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", columnDefinition = "text", nullable = false, unique = true)
    private String token;

    @Column(name = "created_timestamp", nullable = false)
    private Timestamp createdTimestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;


}
