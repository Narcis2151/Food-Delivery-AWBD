package com.unibuc.storeservice.service.impl;

import com.unibuc.storeservice.dto.request.MenuItemRequest;
import com.unibuc.storeservice.dto.response.MenuItemResponse;
import com.unibuc.storeservice.dto.response.PageResponse;
import com.unibuc.storeservice.exception.MenuItemCategoryNotFoundException;
import com.unibuc.storeservice.exception.MenuItemNotFoundException;
import com.unibuc.storeservice.exception.StoreNotFoundException;
import com.unibuc.storeservice.model.MenuItem;
import com.unibuc.storeservice.model.MenuItemCategory;
import com.unibuc.storeservice.model.Store;
import com.unibuc.storeservice.repository.MenuItemCategoryRepository;
import com.unibuc.storeservice.repository.MenuItemRepository;
import com.unibuc.storeservice.repository.StoreRepository;
import com.unibuc.storeservice.security.SecurityUtils;
import com.unibuc.storeservice.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final StoreRepository storeRepository;
    private final MenuItemCategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MenuItemResponse> findAll(Pageable pageable) {
        return PageResponse.from(menuItemRepository.findAll(pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MenuItemResponse> findByStoreId(Long storeId, Pageable pageable) {
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }
        return PageResponse.from(menuItemRepository.findByStoreId(storeId, pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MenuItemResponse> findByCategoryId(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new MenuItemCategoryNotFoundException(categoryId);
        }
        return PageResponse.from(menuItemRepository.findByCategoryId(categoryId, pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse findById(Long id) {
        return toResponse(menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id)));
    }

    @Override
    public MenuItemResponse create(MenuItemRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
        assertIsStoreOwner(store);
        MenuItemCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new MenuItemCategoryNotFoundException(request.getCategoryId()));
        if (!category.getStore().getId().equals(store.getId())) {
            throw new IllegalArgumentException("Category does not belong to the specified store.");
        }
        MenuItem item = MenuItem.builder()
                .store(store)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .isAvailable(request.getIsAvailable())
                .imageUrl(request.getImageUrl())
                .build();
        return toResponse(menuItemRepository.save(item));
    }

    @Override
    public MenuItemResponse update(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
        assertIsStoreOwner(item.getStore());
        MenuItemCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new MenuItemCategoryNotFoundException(request.getCategoryId()));
        if (!category.getStore().getId().equals(item.getStore().getId())) {
            throw new IllegalArgumentException("Category does not belong to this item's store.");
        }
        item.setCategory(category);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setIsAvailable(request.getIsAvailable());
        item.setImageUrl(request.getImageUrl());
        return toResponse(menuItemRepository.save(item));
    }

    @Override
    public void delete(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
        assertIsStoreOwner(item.getStore());
        menuItemRepository.delete(item);
    }

    private void assertIsStoreOwner(Store store) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (!store.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .storeId(item.getStore().getId())
                .storeName(item.getStore().getName())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .isAvailable(item.getIsAvailable())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
