package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.exception.AddressFailureExeption;
import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.repository.AddressRepository;
import com.backend.store.ecommerce.service.contracts.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService implements IAddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAddressesByUser(LocalUser user) {
        return addressRepository.findByUser(user);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressFailureExeption("Address not found"));
    }

    @Override
    @Transactional
    public Address createAddress(Address address, LocalUser user) {
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, Address address) {
        Address existingAddress = getAddressById(id);
        existingAddress.setAdressLine1(address.getAdressLine1());
        existingAddress.setAdressLine2(address.getAdressLine2());
        existingAddress.setCity(address.getCity());
        existingAddress.setCountry(address.getCountry());
        return addressRepository.save(existingAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public boolean validateAddressBelongsToUser(Long addressId, LocalUser user) {
        return addressRepository.findByIdAndUser(addressId, user).isPresent();
    }
}