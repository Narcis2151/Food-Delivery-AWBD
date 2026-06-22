package com.unibuc.storeservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lightweight principal reconstructed from the JWT claims. The stores-service is
 * stateless and never queries the users table: the user id and roles are read
 * straight from the token issued by the auth-service.
 */
@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final Long id;
    private final String email;
}
