package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.MenuItemRequest;
import com.unibuc.backend.dto.response.MenuItemResponse;

import java.util.List;

public interface MenuItemService {
    List<MenuItemResponse> findAll();
    List<MenuItemResponse> findByStoreId(Long storeId);
    List<MenuItemResponse> findByCategoryId(Long categoryId);
    MenuItemResponse findById(Long id);
    MenuItemResponse create(MenuItemRequest request);
    MenuItemResponse update(Long id, MenuItemRequest request);
    void delete(Long id);
}
