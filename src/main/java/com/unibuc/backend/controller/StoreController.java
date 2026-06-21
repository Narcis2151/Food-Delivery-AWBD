package com.unibuc.backend.controller;

import com.unibuc.backend.dto.request.CreateStoreRequest;
import com.unibuc.backend.dto.request.UpdateStoreRequest;
import com.unibuc.backend.dto.response.StoreResponse;
import com.unibuc.backend.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor
@Tag(name = "Stores", description = "Endpoints for managing stores")
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    @Operation(summary = "Get All Stores", description = "Retrieve a list of all stores")
    public ResponseEntity<List<StoreResponse>> findAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Get My Stores", description = "Retrieve the stores owned by the current user")
    public ResponseEntity<List<StoreResponse>> findMine() {
        return ResponseEntity.ok(storeService.findMine());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Store By ID", description = "Retrieve a single store by its ID")
    public ResponseEntity<StoreResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create Store", description = "Create a new store")
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Update Store", description = "Update an existing store by its ID")
    public ResponseEntity<StoreResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateStoreRequest request) {
        return ResponseEntity.ok(storeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Store", description = "Delete a store by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
