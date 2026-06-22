package com.unibuc.authservice.controller;

import com.unibuc.authservice.dto.request.RegisterRequest;
import com.unibuc.authservice.dto.request.UpdateUserRequest;
import com.unibuc.authservice.dto.response.UserResponse;
import com.unibuc.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "Endpoints for the authenticated user profile")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Retrieve the authenticated user's profile")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User By ID", description = "Retrieve a user by id (used for inter-service lookups)")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/me")
    @Operation(summary = "Update Current User", description = "Update the authenticated user's profile and saved address")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUser(request));
    }

    @PostMapping(path = "/store-owners")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create Store Owner", description = "Create a new store owner")
    public ResponseEntity<UserResponse> registerStoreOwner(@Valid @RequestBody RegisterRequest registerUserDto) {
        return ResponseEntity.ok(userService.registerStoreOwner(registerUserDto));
    }

    @GetMapping(path = "/store-owners")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Store Owners", description = "Retrieve a list of all store owners")
    public ResponseEntity<List<UserResponse>> getStoreOwners() {
        return ResponseEntity.ok(userService.getStoreOwners());
    }
}
