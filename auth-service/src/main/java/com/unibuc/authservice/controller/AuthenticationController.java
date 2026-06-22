package com.unibuc.authservice.controller;

import com.unibuc.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.unibuc.authservice.dto.response.LoginResponse;
import com.unibuc.authservice.service.impl.JwtServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unibuc.authservice.dto.request.RegisterRequest;
import com.unibuc.authservice.dto.request.LoginRequest;
import com.unibuc.authservice.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.Map;

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

        String jwtToken = jwtServiceImpl.generateToken(buildClaims(authenticatedUser), authenticatedUser);

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

        String jwtToken = jwtServiceImpl.generateToken(buildClaims(authenticatedUser), authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(
                jwtToken,
                jwtServiceImpl.getExpirationTime()
        );

        return ResponseEntity.ok(loginResponse);
    }

    // Embed the user id and roles in the token so downstream microservices can
    // authorize requests statelessly, without querying the users table.
    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return claims;
    }
}
