package com.unibuc.backend.controller;

import com.unibuc.backend.dto.request.UpdateUserRequest;
import com.unibuc.backend.dto.response.UserResponse;
import com.unibuc.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/me")
    @Operation(summary = "Update Current User", description = "Update the authenticated user's profile and saved address")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUser(request));
    }
}
