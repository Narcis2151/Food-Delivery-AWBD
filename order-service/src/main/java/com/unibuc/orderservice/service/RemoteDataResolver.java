package com.unibuc.orderservice.service;

import com.unibuc.orderservice.client.AuthServiceClient;
import com.unibuc.orderservice.client.StoresServiceClient;
import com.unibuc.orderservice.client.dto.MenuItemDto;
import com.unibuc.orderservice.client.dto.StoreDto;
import com.unibuc.orderservice.client.dto.UserDto;
import com.unibuc.orderservice.exception.MenuItemNotFoundException;
import com.unibuc.orderservice.exception.StoreNotFoundException;
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

    public StoreDto requireStore(Long storeId) {
        try {
            return storesServiceClient.getStoreById(storeId);
        } catch (FeignException.NotFound e) {
            throw new StoreNotFoundException(storeId);
        }
    }

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
