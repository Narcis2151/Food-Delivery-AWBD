package com.unibuc.authservice.controller;

import com.unibuc.authservice.dto.request.AddressRequest;
import com.unibuc.authservice.dto.response.AddressResponse;
import com.unibuc.authservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@AllArgsConstructor
@Tag(name = "Addresses", description = "Endpoints for managing addresses")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get All Addresses", description = "Retrieve a list of all addresses")
    public ResponseEntity<List<AddressResponse>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Address By ID", description = "Retrieve a single address by its ID")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create Address", description = "Create a new address")
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Address", description = "Update an existing address by its ID")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Address", description = "Delete an address by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
