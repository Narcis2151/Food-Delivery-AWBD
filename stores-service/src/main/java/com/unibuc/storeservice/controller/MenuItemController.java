package com.unibuc.storeservice.controller;

import com.unibuc.storeservice.dto.request.MenuItemRequest;
import com.unibuc.storeservice.dto.response.MenuItemResponse;
import com.unibuc.storeservice.dto.response.PageResponse;
import com.unibuc.storeservice.service.MenuItemService;
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
@RequestMapping("/api/v1/menu-items")
@AllArgsConstructor
@Tag(name = "Menu Items", description = "Endpoints for managing menu items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "Get All Menu Items", description = "Retrieve all menu items (paginated)")
    public ResponseEntity<PageResponse<MenuItemResponse>> findAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(menuItemService.findAll(pageable));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get Menu Items By Store", description = "Retrieve menu items for a given store (paginated)")
    public ResponseEntity<PageResponse<MenuItemResponse>> findByStoreId(
            @PathVariable Long storeId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(menuItemService.findByStoreId(storeId, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get Menu Items By Category", description = "Retrieve menu items in a given category (paginated)")
    public ResponseEntity<PageResponse<MenuItemResponse>> findByCategoryId(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(menuItemService.findByCategoryId(categoryId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Menu Item By ID", description = "Retrieve a single menu item by its ID")
    public ResponseEntity<MenuItemResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Create Menu Item", description = "Create a new menu item. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Update Menu Item", description = "Update a menu item. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Delete Menu Item", description = "Delete a menu item. Only the store owner can perform this action.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
