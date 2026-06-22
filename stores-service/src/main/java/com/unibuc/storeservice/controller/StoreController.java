package com.unibuc.storeservice.controller;

import com.unibuc.storeservice.dto.request.CreateStoreRequest;
import com.unibuc.storeservice.dto.request.UpdateStoreRequest;
import com.unibuc.storeservice.dto.response.PageResponse;
import com.unibuc.storeservice.dto.response.StoreResponse;
import com.unibuc.storeservice.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@AllArgsConstructor
@Tag(name = "Stores", description = "Endpoints for managing stores")
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get All Stores", description = "Retrieve a paginated list of stores")
    public ResponseEntity<PageResponse<StoreResponse>> findAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(storeService.findAll(pageable));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Get My Stores", description = "Retrieve the stores owned by the current user")
    public ResponseEntity<StoreResponse> findMine() {
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
        var storeResponse = storeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Update Store", description = "Update an existing store by its ID")
    public ResponseEntity<StoreResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateStoreRequest request) {
        return ResponseEntity.ok(storeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete Store", description = "Delete a store by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
