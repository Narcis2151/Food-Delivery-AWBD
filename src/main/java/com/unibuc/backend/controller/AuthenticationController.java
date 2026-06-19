package com.unibuc.backend.controller;

import com.unibuc.backend.service.impl.AuthenticationServiceImpl;
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

    private final AuthenticationServiceImpl authenticationServiceImpl;


    @PostMapping(path = "/signup")
    @Operation(summary = "Sign Up User", description = "Register a new user")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerUserDto) {
        authenticationServiceImpl.signup(registerUserDto);

        User authenticatedUser = authenticationServiceImpl.authenticate(
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
        User authenticatedUser = authenticationServiceImpl.authenticate(loginUserDto);

        String jwtToken = jwtServiceImpl.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(
                jwtToken,
                jwtServiceImpl.getExpirationTime()
        );

        return ResponseEntity.ok(loginResponse);
    }
}
