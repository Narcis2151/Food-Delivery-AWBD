package com.unibuc.orderservice.service.impl;

import com.unibuc.orderservice.client.AuthServiceClient;
import com.unibuc.orderservice.client.StoresServiceClient;
import com.unibuc.orderservice.client.dto.MenuItemDto;
import com.unibuc.orderservice.client.dto.StoreDto;
import com.unibuc.orderservice.client.dto.UserDto;
import com.unibuc.orderservice.dto.request.OrderItemRequest;
import com.unibuc.orderservice.dto.request.OrderRequest;
import com.unibuc.orderservice.dto.request.OrderStatusUpdateRequest;
import com.unibuc.orderservice.dto.response.OrderItemResponse;
import com.unibuc.orderservice.dto.response.OrderResponse;
import com.unibuc.orderservice.dto.response.PageResponse;
import com.unibuc.orderservice.exception.MenuItemNotFoundException;
import com.unibuc.orderservice.exception.OrderNotFoundException;
import com.unibuc.orderservice.exception.StoreNotFoundException;
import com.unibuc.orderservice.model.Order;
import com.unibuc.orderservice.model.OrderItem;
import com.unibuc.orderservice.model.OrderStatus;
import com.unibuc.orderservice.repository.OrderRepository;
import com.unibuc.orderservice.security.AuthenticatedUser;
import com.unibuc.orderservice.security.SecurityUtils;
import com.unibuc.orderservice.service.OrderService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StoresServiceClient storesServiceClient;
    private final AuthServiceClient authServiceClient;

    @Override
    public OrderResponse create(OrderRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.currentUser();

        StoreDto store = fetchStore(request.getStoreId());

        Order order = Order.builder()
                .userId(currentUser.getId())
                .storeId(store.getId())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            MenuItemDto menuItem = fetchMenuItem(itemReq.getMenuItemId());
            if (!menuItem.getStoreId().equals(store.getId())) {
                throw new IllegalArgumentException(
                        "Menu item " + menuItem.getId() + " does not belong to store " + store.getId());
            }
            if (!menuItem.getIsAvailable()) {
                throw new IllegalArgumentException("Menu item '" + menuItem.getName() + "' is not available.");
            }
            return OrderItem.builder()
                    .order(order)
                    .menuItemId(menuItem.getId())
                    .menuItemName(menuItem.getName())
                    .quantity(itemReq.getQuantity())
                    .price(menuItem.getPrice())
                    .build();
        }).toList();

        order.setItems(items);
        order.setTotalAmount(items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return toResponse(orderRepository.save(order), store.getName(), resolveUserFullName(currentUser.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findMyOrders(List<OrderStatus> statuses, Pageable pageable) {
        Long userId = SecurityUtils.currentUser().getId();
        Page<Order> page = statuses == null || statuses.isEmpty()
                ? orderRepository.findByUserId(userId, pageable)
                : orderRepository.findByUserIdAndOrderStatusIn(userId, statuses, pageable);
        return PageResponse.from(page, order -> toResponse(order,
                resolveStoreName(order.getStoreId()),
                resolveUserFullName(order.getUserId())));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findByStoreId(Long storeId, List<OrderStatus> statuses, Pageable pageable) {
        StoreDto store = fetchStore(storeId);
        assertIsStoreOwner(store);
        Page<Order> page = statuses == null || statuses.isEmpty()
                ? orderRepository.findByStoreId(storeId, pageable)
                : orderRepository.findByStoreIdAndOrderStatusIn(storeId, statuses, pageable);
        return PageResponse.from(page, order -> toResponse(order, store.getName(),
                resolveUserFullName(order.getUserId())));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        assertCanViewOrder(order);
        return toResponse(order, resolveStoreName(order.getStoreId()), resolveUserFullName(order.getUserId()));
    }

    @Override
    public OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        StoreDto store = fetchStore(order.getStoreId());
        assertIsStoreOwner(store);
        order.setOrderStatus(request.getOrderStatus());
        return toResponse(orderRepository.save(order), store.getName(), resolveUserFullName(order.getUserId()));
    }

    private StoreDto fetchStore(Long storeId) {
        try {
            return storesServiceClient.getStoreById(storeId);
        } catch (FeignException.NotFound e) {
            throw new StoreNotFoundException(storeId);
        }
    }

    private MenuItemDto fetchMenuItem(Long menuItemId) {
        try {
            return storesServiceClient.getMenuItemById(menuItemId);
        } catch (FeignException.NotFound e) {
            throw new MenuItemNotFoundException(menuItemId);
        }
    }

    private void assertIsStoreOwner(StoreDto store) {
        AuthenticatedUser currentUser = SecurityUtils.currentUser();
        if (!store.getOwnerId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }
    }

    private void assertCanViewOrder(Order order) {
        AuthenticatedUser currentUser = SecurityUtils.currentUser();
        if (order.getUserId().equals(currentUser.getId())) {
            return;
        }
        StoreDto store = fetchStore(order.getStoreId());
        if (!store.getOwnerId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this order.");
        }
    }

    private String resolveStoreName(Long storeId) {
        try {
            return storesServiceClient.getStoreById(storeId).getName();
        } catch (Exception e) {
            log.warn("Could not resolve store name for storeId={}: {}", storeId, e.getMessage());
            return null;
        }
    }

    private String resolveUserFullName(Long userId) {
        try {
            return authServiceClient.getUserById(userId).getFullName();
        } catch (Exception e) {
            log.warn("Could not resolve full name for userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItemId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private OrderResponse toResponse(Order order, String storeName, String userFullName) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userFullName(userFullName)
                .storeId(order.getStoreId())
                .storeName(storeName)
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }
}
