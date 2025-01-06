package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.LocalUser;

import java.util.List;

public interface IAddressService {
    List<Address> getAddressesByUser(LocalUser user);

    Address getAddressById(Long id);

    Address createAddress(Address address, LocalUser user);

    Address updateAddress(Long id, Address address);

    void deleteAddress(Long id);

    boolean validateAddressBelongsToUser(Long addressId, LocalUser user);
}