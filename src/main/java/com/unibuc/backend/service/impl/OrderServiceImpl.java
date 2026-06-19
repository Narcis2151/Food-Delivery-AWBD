package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.OrderRequest;
import com.unibuc.backend.dto.request.OrderStatusUpdateRequest;
import com.unibuc.backend.dto.response.OrderItemResponse;
import com.unibuc.backend.dto.response.OrderResponse;
import com.unibuc.backend.exception.MenuItemNotFoundException;
import com.unibuc.backend.exception.OrderNotFoundException;
import com.unibuc.backend.exception.StoreNotFoundException;
import com.unibuc.backend.model.*;
import com.unibuc.backend.repository.MenuItemRepository;
import com.unibuc.backend.repository.OrderRepository;
import com.unibuc.backend.repository.StoreRepository;
import com.unibuc.backend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public OrderResponse create(OrderRequest request) {
        User customer = getCurrentUser();
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException(request.getStoreId()));

        Order order = Order.builder()
                .user(customer)
                .store(store)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new MenuItemNotFoundException(itemReq.getMenuItemId()));
            if (!menuItem.getStore().getId().equals(store.getId())) {
                throw new IllegalArgumentException(
                        "Menu item " + menuItem.getId() + " does not belong to store " + store.getId());
            }
            if (!menuItem.getIsAvailable()) {
                throw new IllegalArgumentException("Menu item '" + menuItem.getName() + "' is not available.");
            }
            return OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemReq.getQuantity())
                    .price(menuItem.getPrice())
                    .build();
        }).toList();

        order.setItems(items);
        order.setTotalAmount(items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        assertIsStoreOwner(store);
        return orderRepository.findByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        assertCanViewOrder(order);
        return toResponse(order);
    }

    @Override
    public OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        assertIsStoreOwner(order.getStore());
        order.setOrderStatus(request.getOrderStatus());
        return toResponse(orderRepository.save(order));
    }

    private void assertIsStoreOwner(Store store) {
        User currentUser = getCurrentUser();
        if (!store.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }
    }

    private void assertCanViewOrder(Order order) {
        User currentUser = getCurrentUser();
        boolean isCustomer = order.getUser().getId().equals(currentUser.getId());
        boolean isStoreOwner = order.getStore().getOwner().getId().equals(currentUser.getId());
        if (!isCustomer && !isStoreOwner) {
            throw new AccessDeniedException("You do not have access to this order.");
        }
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userFullName(order.getUser().getFullName())
                .storeId(order.getStore().getId())
                .storeName(order.getStore().getName())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }
}
