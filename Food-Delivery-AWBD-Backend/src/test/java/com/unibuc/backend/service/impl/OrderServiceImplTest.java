package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.OrderItemRequest;
import com.unibuc.backend.dto.request.OrderRequest;
import com.unibuc.backend.dto.request.OrderStatusUpdateRequest;
import com.unibuc.backend.dto.response.OrderResponse;
import com.unibuc.backend.exception.MenuItemNotFoundException;
import com.unibuc.backend.exception.OrderNotFoundException;
import com.unibuc.backend.exception.StoreNotFoundException;
import com.unibuc.backend.model.*;
import com.unibuc.backend.repository.MenuItemRepository;
import com.unibuc.backend.repository.OrderRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock StoreRepository storeRepository;
    @Mock MenuItemRepository menuItemRepository;

    @InjectMocks OrderServiceImpl orderService;

    private User customer;
    private User storeOwner;
    private Store store;

    @BeforeEach
    void setUp() {
        customer  = User.builder().id(1L).fullName("Customer").email("customer@mail.com").password("pw").build();
        storeOwner = User.builder().id(2L).fullName("Owner").email("owner@mail.com").password("pw").build();
        store = Store.builder().id(10L).name("Pizza Place").owner(storeOwner).build();
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

    private MenuItem availableItem(Long id, String name, BigDecimal price) {
        return MenuItem.builder().id(id).name(name).price(price).isAvailable(true).store(store).build();
    }

    @Test
    void create_whenStoreNotFound_throwsStoreNotFoundException() {
        authenticateAs(customer);
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(new OrderRequest(99L, List.of(new OrderItemRequest(1L, 1)))))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void create_whenMenuItemNotFound_throwsMenuItemNotFoundException() {
        authenticateAs(customer);
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(new OrderRequest(10L, List.of(new OrderItemRequest(99L, 1)))))
                .isInstanceOf(MenuItemNotFoundException.class);
    }

    @Test
    void create_whenMenuItemBelongsToDifferentStore_throwsIllegalArgumentException() {
        authenticateAs(customer);
        Store otherStore = Store.builder().id(20L).name("Other Store").owner(storeOwner).build();
        MenuItem foreignItem = MenuItem.builder().id(1L).name("Burger").price(new BigDecimal("10.00"))
                .isAvailable(true).store(otherStore).build();
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(foreignItem));

        assertThatThrownBy(() -> orderService.create(new OrderRequest(10L, List.of(new OrderItemRequest(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to store");
    }

    @Test
    void create_whenMenuItemNotAvailable_throwsIllegalArgumentException() {
        authenticateAs(customer);
        MenuItem unavailable = MenuItem.builder().id(1L).name("Sold Out").price(new BigDecimal("5.00"))
                .isAvailable(false).store(store).build();
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(unavailable));

        assertThatThrownBy(() -> orderService.create(new OrderRequest(10L, List.of(new OrderItemRequest(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void create_whenValid_calculatesCorrectTotalAmount() {
        authenticateAs(customer);
        MenuItem pizza = availableItem(1L, "Pizza", new BigDecimal("10.00"));
        MenuItem coke  = availableItem(2L, "Coke",  new BigDecimal("3.50"));
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(pizza));
        when(menuItemRepository.findById(2L)).thenReturn(Optional.of(coke));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.create(new OrderRequest(10L, List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 1)
        )));

        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("23.50"));
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void findById_whenOrderNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void findById_whenCalledByOrderCustomer_returnsOrderResponse() {
        authenticateAs(customer);
        Order order = Order.builder().id(1L).user(customer).store(store)
                .orderStatus(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(1L);

        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void findById_whenCalledByStoreOwner_returnsOrderResponse() {
        authenticateAs(storeOwner);
        Order order = Order.builder().id(1L).user(customer).store(store)
                .orderStatus(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(1L);

        assertThat(response.getStoreId()).isEqualTo(10L);
    }

    @Test
    void findById_whenCalledByUnrelatedUser_throwsAccessDeniedException() {
        User stranger = User.builder().id(3L).fullName("Stranger").email("s@mail.com").password("pw").build();
        authenticateAs(stranger);
        Order order = Order.builder().id(1L).user(customer).store(store)
                .orderStatus(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateStatus_whenNotStoreOwner_throwsAccessDeniedException() {
        authenticateAs(customer);
        Order order = Order.builder().id(1L).user(customer).store(store)
                .orderStatus(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L, new OrderStatusUpdateRequest(OrderStatus.PREPARING)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateStatus_whenStoreOwner_updatesOrderStatus() {
        authenticateAs(storeOwner);
        Order order = Order.builder().id(1L).user(customer).store(store)
                .orderStatus(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.updateStatus(1L, new OrderStatusUpdateRequest(OrderStatus.PREPARING));

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void updateStatus_whenOrderNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(99L, new OrderStatusUpdateRequest(OrderStatus.CANCELLED)))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
