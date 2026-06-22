package com.unibuc.storeservice.service;

import com.unibuc.storeservice.client.AuthServiceClient;
import com.unibuc.storeservice.client.dto.UserDto;
import com.unibuc.storeservice.exception.OwnerNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves store owner information from the auth-service over OpenFeign.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerResolver {

    private final AuthServiceClient authServiceClient;

    /** Validates that the owner exists in the auth-service, returning its details. */
    public UserDto requireOwner(Long ownerId) {
        try {
            return authServiceClient.getUserById(ownerId);
        } catch (FeignException.NotFound e) {
            throw new OwnerNotFoundException(ownerId);
        }
    }

    /** Best-effort owner email lookup for read responses; null when unavailable. */
    public String resolveEmail(Long ownerId) {
        try {
            UserDto owner = authServiceClient.getUserById(ownerId);
            return owner != null ? owner.getEmail() : null;
        } catch (Exception e) {
            log.warn("Could not resolve owner {} from auth-service: {}", ownerId, e.getMessage());
            return null;
        }
    }
}
