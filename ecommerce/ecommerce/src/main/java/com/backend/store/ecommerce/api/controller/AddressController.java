package com.backend.store.ecommerce.api.controller;

import com.backend.store.ecommerce.api.model.DTOs.AddressDTO;
import com.backend.store.ecommerce.api.model.Requests.AddressCreateRequest;
import com.backend.store.ecommerce.api.model.Requests.AddressUpdateRequest;
import com.backend.store.ecommerce.mapper.AddressMapper;
import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.service.contracts.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address Controller", description = "Address management APIs")
@Validated
public class AddressController {
    private final IAddressService addressService;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressController(IAddressService addressService, AddressMapper addressMapper) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;
    }

    @Operation(summary = "Get user addresses",
            description = "Retrieves all addresses for the current user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses")
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(
            @AuthenticationPrincipal LocalUser user) {
        List<Address> addresses = addressService.getAddressesByUser(user);
        List<AddressDTO> dtos = addresses.stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get address by ID",
            description = "Retrieves a specific address by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved address")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal LocalUser user) {

        Address address = addressService.getAddressById(id);

        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to view this address");
        }

        return ResponseEntity.ok(addressMapper.toDTO(address));
    }

    @Operation(summary = "Create address",
            description = "Creates a new address for the current user")
    @ApiResponse(responseCode = "201", description = "Address successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @Valid @RequestBody AddressCreateRequest request,
            @AuthenticationPrincipal LocalUser user) {

        Address address = addressMapper.toEntity(request);
        Address created = addressService.createAddress(address, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(addressMapper.toDTO(created));
    }

    @Operation(summary = "Update address",
            description = "Updates an existing address")
    @ApiResponse(responseCode = "200", description = "Address successfully updated")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody AddressUpdateRequest request,
            @AuthenticationPrincipal LocalUser user) {

        if (!addressService.validateAddressBelongsToUser(id, user)) {
            throw new AccessDeniedException("Not authorized to update this address");
        }

        Address address = addressMapper.toEntity(request);
        Address updated = addressService.updateAddress(id, address);
        return ResponseEntity.ok(addressMapper.toDTO(updated));
    }

    @Operation(summary = "Delete address",
            description = "Deletes an address")
    @ApiResponse(responseCode = "204", description = "Address successfully deleted")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal LocalUser user) {

        if (!addressService.validateAddressBelongsToUser(id, user)) {
            throw new AccessDeniedException("Not authorized to delete this address");
        }

        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}