package com.unibuc.storeservice.service;

import com.unibuc.storeservice.dto.request.MenuItemCategoryRequest;
import com.unibuc.storeservice.dto.response.MenuItemCategoryResponse;

import java.util.List;

public interface MenuItemCategoryService {
    List<MenuItemCategoryResponse> findAll();
    List<MenuItemCategoryResponse> findByStoreId(Long storeId);
    MenuItemCategoryResponse findById(Long id);
    MenuItemCategoryResponse create(MenuItemCategoryRequest request);
    MenuItemCategoryResponse update(Long id, MenuItemCategoryRequest request);
    void delete(Long id);
}
