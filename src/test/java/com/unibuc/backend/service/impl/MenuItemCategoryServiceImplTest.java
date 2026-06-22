package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.MenuItemCategoryRequest;
import com.unibuc.backend.dto.response.MenuItemCategoryResponse;
import com.unibuc.backend.exception.MenuItemCategoryNotFoundException;
import com.unibuc.backend.exception.StoreNotFoundException;
import com.unibuc.backend.model.MenuItemCategory;
import com.unibuc.backend.model.Store;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.MenuItemCategoryRepository;
import com.unibuc.backend.repository.StoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemCategoryServiceImplTest {

    @Mock MenuItemCategoryRepository categoryRepository;
    @Mock StoreRepository storeRepository;

    @InjectMocks MenuItemCategoryServiceImpl categoryService;

    private User storeOwner;
    private User nonOwner;
    private Store store;

    @BeforeEach
    void setUp() {
        storeOwner = User.builder().id(1L).fullName("Owner").email("owner@mail.com").password("pw").build();
        nonOwner   = User.builder().id(2L).fullName("Other").email("other@mail.com").password("pw").build();
        store = Store.builder().id(10L).name("Burger Barn").owner(storeOwner).build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void findByStoreId_whenStoreNotFound_throwsStoreNotFoundException() {
        when(storeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.findByStoreId(99L))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void findByStoreId_whenStoreExists_returnsMappedCategories() {
        MenuItemCategory cat = MenuItemCategory.builder().id(1L).store(store).name("Burgers").displayOrder(1).build();
        when(storeRepository.existsById(10L)).thenReturn(true);
        when(categoryRepository.findByStoreId(10L)).thenReturn(List.of(cat));

        List<MenuItemCategoryResponse> result = categoryService.findByStoreId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Burgers");
    }

    @Test
    void findById_whenCategoryNotFound_throwsMenuItemCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(MenuItemCategoryNotFoundException.class);
    }

    @Test
    void create_whenStoreNotFound_throwsStoreNotFoundException() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.create(new MenuItemCategoryRequest(99L, "Drinks", 1)))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void create_whenNotStoreOwner_throwsAccessDeniedException() {
        authenticateAs(nonOwner);
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));

        assertThatThrownBy(() -> categoryService.create(new MenuItemCategoryRequest(10L, "Drinks", 1)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void create_whenStoreOwner_returnsCreatedCategory() {
        authenticateAs(storeOwner);
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(categoryRepository.save(any(MenuItemCategory.class))).thenAnswer(inv -> {
            MenuItemCategory c = inv.getArgument(0);
            return MenuItemCategory.builder().id(1L).store(c.getStore()).name(c.getName()).displayOrder(c.getDisplayOrder()).build();
        });

        MenuItemCategoryResponse response = categoryService.create(new MenuItemCategoryRequest(10L, "Drinks", 2));

        assertThat(response.getName()).isEqualTo("Drinks");
        assertThat(response.getDisplayOrder()).isEqualTo(2);
        assertThat(response.getStoreId()).isEqualTo(10L);
    }

    @Test
    void update_whenCategoryNotFound_throwsMenuItemCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, new MenuItemCategoryRequest(10L, "Updated", 1)))
                .isInstanceOf(MenuItemCategoryNotFoundException.class);
    }

    @Test
    void update_whenNotStoreOwner_throwsAccessDeniedException() {
        authenticateAs(nonOwner);
        MenuItemCategory existing = MenuItemCategory.builder().id(1L).store(store).name("Burgers").displayOrder(1).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> categoryService.update(1L, new MenuItemCategoryRequest(10L, "Updated", 2)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_whenStoreOwner_updatesNameAndDisplayOrder() {
        authenticateAs(storeOwner);
        MenuItemCategory existing = MenuItemCategory.builder().id(1L).store(store).name("Burgers").displayOrder(1).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(MenuItemCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItemCategoryResponse response = categoryService.update(1L, new MenuItemCategoryRequest(10L, "Sandwiches", 3));

        assertThat(response.getName()).isEqualTo("Sandwiches");
        assertThat(response.getDisplayOrder()).isEqualTo(3);
    }

    @Test
    void delete_whenNotStoreOwner_throwsAccessDeniedException() {
        authenticateAs(nonOwner);
        MenuItemCategory existing = MenuItemCategory.builder().id(1L).store(store).name("Burgers").displayOrder(1).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(AccessDeniedException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_whenStoreOwner_deletesCategoryFromRepository() {
        authenticateAs(storeOwner);
        MenuItemCategory existing = MenuItemCategory.builder().id(1L).store(store).name("Burgers").displayOrder(1).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        categoryService.delete(1L);

        verify(categoryRepository).delete(existing);
    }
}
