package com.unibuc.storeservice.controller;

import com.unibuc.storeservice.dto.request.MenuItemCategoryRequest;
import com.unibuc.storeservice.dto.response.MenuItemCategoryResponse;
import com.unibuc.storeservice.service.MenuItemCategoryService;
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
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
@Tag(name = "Menu Item Categories", description = "Endpoints for managing menu item categories")
public class MenuItemCategoryController {
    private final MenuItemCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Retrieve all menu item categories")
    public ResponseEntity<List<MenuItemCategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get Categories By Store", description = "Retrieve all categories for a given store")
    public ResponseEntity<List<MenuItemCategoryResponse>> findByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(categoryService.findByStoreId(storeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category By ID", description = "Retrieve a single category by its ID")
    public ResponseEntity<MenuItemCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Create Category", description = "Create a new category for a store. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemCategoryResponse> create(@Valid @RequestBody MenuItemCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Update Category", description = "Update a category's name or display order. Only the store owner can perform this action.")
    public ResponseEntity<MenuItemCategoryResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody MenuItemCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STORE_OWNER')")
    @Operation(summary = "Delete Category", description = "Delete a category. Only the store owner can perform this action.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
