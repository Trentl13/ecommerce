package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.Address;
import org.springframework.data.repository.ListCrudRepository;

public interface AddressRepository extends ListCrudRepository<Address, Long> {

}
