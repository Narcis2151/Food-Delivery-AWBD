package com.unibuc.reviewservice.service;

import com.unibuc.reviewservice.client.AuthServiceClient;
import com.unibuc.reviewservice.client.StoresServiceClient;
import com.unibuc.reviewservice.client.dto.MenuItemDto;
import com.unibuc.reviewservice.client.dto.UserDto;
import com.unibuc.reviewservice.exception.MenuItemNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves data owned by other microservices (stores-service, auth-service)
 * over OpenFeign, translating remote 404s into local domain exceptions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RemoteDataResolver {

    private final StoresServiceClient storesServiceClient;
    private final AuthServiceClient authServiceClient;

    public MenuItemDto requireMenuItem(Long menuItemId) {
        try {
            return storesServiceClient.getMenuItemById(menuItemId);
        } catch (FeignException.NotFound e) {
            throw new MenuItemNotFoundException(menuItemId);
        }
    }

    public String resolveUserFullName(Long userId) {
        try {
            UserDto user = authServiceClient.getUserById(userId);
            return user != null ? user.getFullName() : null;
        } catch (Exception e) {
            log.warn("Could not resolve user {} from auth-service: {}", userId, e.getMessage());
            return null;
        }
    }
}
