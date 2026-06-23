package com.unibuc.reviewservice.client;

import com.unibuc.reviewservice.client.dto.MenuItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stores-service")
public interface StoresServiceClient {

    @GetMapping("/api/v1/menu-items/{id}")
    MenuItemDto getMenuItemById(@PathVariable("id") Long id);
}
