package com.unibuc.storeservice.service.impl;

import com.unibuc.storeservice.dto.request.MenuItemCategoryRequest;
import com.unibuc.storeservice.dto.response.MenuItemCategoryResponse;
import com.unibuc.storeservice.exception.MenuItemCategoryNotFoundException;
import com.unibuc.storeservice.exception.StoreNotFoundException;
import com.unibuc.storeservice.model.MenuItemCategory;
import com.unibuc.storeservice.model.Store;
import com.unibuc.storeservice.repository.MenuItemCategoryRepository;
import com.unibuc.storeservice.repository.StoreRepository;
import com.unibuc.storeservice.security.SecurityUtils;
import com.unibuc.storeservice.service.MenuItemCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class MenuItemCategoryServiceImpl implements MenuItemCategoryService {
    private final MenuItemCategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemCategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemCategoryResponse> findByStoreId(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }
        return categoryRepository.findByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemCategoryResponse findById(Long id) {
        return toResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new MenuItemCategoryNotFoundException(id)));
    }

    @Override
    public MenuItemCategoryResponse create(MenuItemCategoryRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));
        assertIsStoreOwner(store);
        MenuItemCategory category = MenuItemCategory.builder()
                .store(store)
                .name(request.getName())
                .displayOrder(request.getDisplayOrder())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public MenuItemCategoryResponse update(Long id, MenuItemCategoryRequest request) {
        MenuItemCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new MenuItemCategoryNotFoundException(id));
        assertIsStoreOwner(category.getStore());
        category.setName(request.getName());
        category.setDisplayOrder(request.getDisplayOrder());
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        MenuItemCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new MenuItemCategoryNotFoundException(id));
        assertIsStoreOwner(category.getStore());
        categoryRepository.delete(category);
    }

    private void assertIsStoreOwner(Store store) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (!store.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }
    }

    private MenuItemCategoryResponse toResponse(MenuItemCategory category) {
        return MenuItemCategoryResponse.builder()
                .id(category.getId())
                .storeId(category.getStore().getId())
                .storeName(category.getStore().getName())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
