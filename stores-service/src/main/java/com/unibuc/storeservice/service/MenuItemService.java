package com.unibuc.storeservice.service;

import com.unibuc.storeservice.dto.request.MenuItemRequest;
import com.unibuc.storeservice.dto.response.MenuItemResponse;
import com.unibuc.storeservice.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {
    PageResponse<MenuItemResponse> findAll(Pageable pageable);
    PageResponse<MenuItemResponse> findByStoreId(Long storeId, Pageable pageable);
    PageResponse<MenuItemResponse> findByCategoryId(Long categoryId, Pageable pageable);
    MenuItemResponse findById(Long id);
    MenuItemResponse create(MenuItemRequest request);
    MenuItemResponse update(Long id, MenuItemRequest request);
    void delete(Long id);
}
