package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface LocalUserRepository extends ListCrudRepository<LocalUser, Long> {
//Ignore Case да не гледа буквите дали са главни или малки
    Optional<LocalUser> findByUsernameIgnoreCase(String username);
    Optional<LocalUser> findByEmailIgnoreCase(String email);

}
