package com.backend.store.ecommerce.mapper;

import com.backend.store.ecommerce.api.model.DTOs.AddressDTO;
import com.backend.store.ecommerce.api.model.DTOs.AddressDetailDTO;
import com.backend.store.ecommerce.api.model.Requests.AddressCreateRequest;
import com.backend.store.ecommerce.api.model.Requests.AddressUpdateRequest;
import com.backend.store.ecommerce.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAdressLine1());
        dto.setAddressLine2(address.getAdressLine2());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        return dto;
    }

    public Address toEntity(AddressCreateRequest request) {
        Address address = new Address();
        address.setAdressLine1(request.getAddressLine1());
        address.setAdressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        return address;
    }

    public Address toEntity(AddressUpdateRequest request) {
        Address address = new Address();
        address.setAdressLine1(request.getAddressLine1());
        address.setAdressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        return address;
    }

    public AddressDetailDTO toDetailDTO(Address address) {
        AddressDetailDTO dto = new AddressDetailDTO();
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAdressLine1());
        dto.setAddressLine2(address.getAdressLine2());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());

        dto.setUserId(address.getUser().getId());
        dto.setUsername(address.getUser().getUsername());


        return dto;
    }
}