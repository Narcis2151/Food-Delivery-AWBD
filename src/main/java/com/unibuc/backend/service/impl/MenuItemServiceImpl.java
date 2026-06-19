package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.MenuItemRequest;
import com.unibuc.backend.dto.response.MenuItemResponse;
import com.unibuc.backend.exception.MenuItemCategoryNotFoundException;
import com.unibuc.backend.exception.MenuItemNotFoundException;
import com.unibuc.backend.exception.StoreNotFoundException;
import com.unibuc.backend.model.MenuItem;
import com.unibuc.backend.model.MenuItemCategory;
import com.unibuc.backend.model.Store;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.MenuItemCategoryRepository;
import com.unibuc.backend.repository.MenuItemRepository;
import com.unibuc.backend.repository.StoreRepository;
import com.unibuc.backend.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final StoreRepository storeRepository;
    private final MenuItemCategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> findAll() {
        return menuItemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> findByStoreId(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }
        return menuItemRepository.findByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new MenuItemCategoryNotFoundException(categoryId);
        }
        return menuItemRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .toList();
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
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!store.getOwner().getId().equals(currentUser.getId())) {
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
