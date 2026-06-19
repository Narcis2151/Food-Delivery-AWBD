package com.unibuc.backend.controller;

import com.unibuc.backend.dto.request.MenuItemRequest;
import com.unibuc.backend.dto.response.MenuItemResponse;
import com.unibuc.backend.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@AllArgsConstructor
@Tag(name = "Menu Items", description = "Endpoints for managing menu items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "Get All Menu Items", description = "Retrieve all menu items")
    public ResponseEntity<List<MenuItemResponse>> findAll() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get Menu Items By Store", description = "Retrieve all menu items for a given store")
    public ResponseEntity<List<MenuItemResponse>> findByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(menuItemService.findByStoreId(storeId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get Menu Items By Category", description = "Retrieve all menu items in a given category")
    public ResponseEntity<List<MenuItemResponse>> findByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuItemService.findByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Menu Item By ID", description = "Retrieve a single menu item by its ID")
    public ResponseEntity<MenuItemResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create Menu Item", description = "Create a new menu item. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Menu Item", description = "Update a menu item. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Menu Item", description = "Delete a menu item. Only the store owner can perform this action.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
