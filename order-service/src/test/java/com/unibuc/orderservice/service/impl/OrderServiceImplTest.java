package com.unibuc.orderservice.service.impl;

import com.unibuc.orderservice.client.dto.MenuItemDto;
import com.unibuc.orderservice.client.dto.StoreDto;
import com.unibuc.orderservice.dto.request.OrderItemRequest;
import com.unibuc.orderservice.dto.request.OrderRequest;
import com.unibuc.orderservice.dto.response.OrderResponse;
import com.unibuc.orderservice.model.Order;
import com.unibuc.orderservice.model.OrderStatus;
import com.unibuc.orderservice.repository.OrderRepository;
import com.unibuc.orderservice.security.AuthenticatedUser;
import com.unibuc.orderservice.service.RemoteDataResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RemoteDataResolver remoteDataResolver;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(Long userId) {
        AuthenticatedUser principal = new AuthenticatedUser(userId, "customer@mail.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }

    @Test
    void create_buildsOrderFromRemoteStoreAndMenuItems() {
        authenticateAs(10L);
        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(100L, 2)));

        when(remoteDataResolver.requireStore(1L))
                .thenReturn(StoreDto.builder().id(1L).name("Pizza Palace").ownerId(5L).build());
        when(remoteDataResolver.resolveUserFullName(10L)).thenReturn("John Doe");
        when(remoteDataResolver.requireMenuItem(100L)).thenReturn(MenuItemDto.builder()
                .id(100L).storeId(1L).name("Margherita").price(new BigDecimal("20.00")).isAvailable(true).build());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(50L);
            return o;
        });

        OrderResponse response = orderService.create(request);

        assertEquals(50L, response.getId());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
        assertEquals(new BigDecimal("40.00"), response.getTotalAmount());
        assertEquals("John Doe", response.getUserFullName());
        assertEquals(1, response.getItems().size());
        assertEquals("Margherita", response.getItems().get(0).getMenuItemName());
    }

    @Test
    void create_rejectsMenuItemFromAnotherStore() {
        authenticateAs(10L);
        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(100L, 1)));
        when(remoteDataResolver.requireStore(1L))
                .thenReturn(StoreDto.builder().id(1L).name("Pizza Palace").ownerId(5L).build());
        when(remoteDataResolver.resolveUserFullName(10L)).thenReturn("John Doe");
        when(remoteDataResolver.requireMenuItem(100L)).thenReturn(MenuItemDto.builder()
                .id(100L).storeId(99L).name("Sushi").price(new BigDecimal("30.00")).isAvailable(true).build());

        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    void findByStoreId_deniesNonOwner() {
        authenticateAs(10L);
        when(remoteDataResolver.requireStore(1L))
                .thenReturn(StoreDto.builder().id(1L).name("Pizza Palace").ownerId(5L).build());

        assertThrows(AccessDeniedException.class,
                () -> orderService.findByStoreId(1L, null, org.springframework.data.domain.Pageable.unpaged()));
    }
}
