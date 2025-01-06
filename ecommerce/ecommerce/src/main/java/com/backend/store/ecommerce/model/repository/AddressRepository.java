package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends ListCrudRepository<Address, Long> {
    List<Address> findByUser(LocalUser user);

    Optional<Address> findByIdAndUser(Long id, LocalUser user);
}