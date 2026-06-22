package com.unibuc.backend.controller;

import com.unibuc.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.unibuc.backend.dto.response.LoginResponse;
import com.unibuc.backend.service.impl.JwtServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.dto.request.LoginRequest;
import com.unibuc.backend.model.User;

@RequestMapping("/api/v1/auth")
@RestController
@AllArgsConstructor
@SecurityRequirements()
@Tag(name = "Authentication", description = "Endpoints for managing user authentication")
public class AuthenticationController {
    private final JwtServiceImpl jwtServiceImpl;

    private final AuthenticationService authenticationService;


    @PostMapping(path = "/register")
    @Operation(summary = "Sign Up Customer", description = "Register a new customer")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerUserDto) {
        authenticationService.registerCustomer(registerUserDto);

        User authenticatedUser = authenticationService.authenticate(
                new LoginRequest(registerUserDto.getEmail(), registerUserDto.getPassword())
        );

        String jwtToken = jwtServiceImpl.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(
                jwtToken,
                jwtServiceImpl.getExpirationTime()
        );

        return ResponseEntity.ok(loginResponse);
    }



    @PostMapping(path = "/login")
    @Operation(summary = "Login User", description = "Authenticate a user")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtServiceImpl.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(
                jwtToken,
                jwtServiceImpl.getExpirationTime()
        );

        return ResponseEntity.ok(loginResponse);
    }
}
