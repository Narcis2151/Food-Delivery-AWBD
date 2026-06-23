package com.unibuc.orderservice.client;

import com.unibuc.orderservice.client.dto.MenuItemDto;
import com.unibuc.orderservice.client.dto.StoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stores-service")
public interface StoresServiceClient {

    @GetMapping("/api/v1/stores/{id}")
    StoreDto getStoreById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/menu-items/{id}")
    MenuItemDto getMenuItemById(@PathVariable("id") Long id);
}
