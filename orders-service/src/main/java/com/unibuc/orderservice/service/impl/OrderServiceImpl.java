package com.unibuc.orderservice.service.impl;

import com.unibuc.orderservice.client.dto.MenuItemDto;
import com.unibuc.orderservice.client.dto.StoreDto;
import com.unibuc.orderservice.dto.request.OrderRequest;
import com.unibuc.orderservice.dto.request.OrderStatusUpdateRequest;
import com.unibuc.orderservice.dto.response.OrderItemResponse;
import com.unibuc.orderservice.dto.response.OrderResponse;
import com.unibuc.orderservice.dto.response.PageResponse;
import com.unibuc.orderservice.exception.OrderNotFoundException;
import com.unibuc.orderservice.model.Order;
import com.unibuc.orderservice.model.OrderItem;
import com.unibuc.orderservice.model.OrderStatus;
import com.unibuc.orderservice.repository.OrderRepository;
import com.unibuc.orderservice.security.SecurityUtils;
import com.unibuc.orderservice.service.OrderService;
import com.unibuc.orderservice.service.RemoteDataResolver;
import lombok.AllArgsConstructor;
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
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RemoteDataResolver remoteDataResolver;

    @Override
    public OrderResponse create(OrderRequest request) {
        Long customerId = SecurityUtils.currentUser().getId();
        StoreDto store = remoteDataResolver.requireStore(request.getStoreId());
        String customerName = remoteDataResolver.resolveUserFullName(customerId);

        Order order = Order.builder()
                .userId(customerId)
                .userFullName(customerName)
                .storeId(store.getId())
                .storeName(store.getName())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            MenuItemDto menuItem = remoteDataResolver.requireMenuItem(itemReq.getMenuItemId());
            if (!store.getId().equals(menuItem.getStoreId())) {
                throw new IllegalArgumentException(
                        "Menu item " + menuItem.getId() + " does not belong to store " + store.getId());
            }
            if (Boolean.FALSE.equals(menuItem.getIsAvailable())) {
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

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findMyOrders(List<OrderStatus> statuses, Pageable pageable) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        Page<Order> page = statuses == null || statuses.isEmpty()
                ? orderRepository.findByUserId(currentUserId, pageable)
                : orderRepository.findByUserIdAndOrderStatusIn(currentUserId, statuses, pageable);
        return PageResponse.from(page, this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findByStoreId(Long storeId, List<OrderStatus> statuses, Pageable pageable) {
        StoreDto store = remoteDataResolver.requireStore(storeId);
        assertIsStoreOwner(store.getOwnerId());
        Page<Order> page = statuses == null || statuses.isEmpty()
                ? orderRepository.findByStoreId(storeId, pageable)
                : orderRepository.findByStoreIdAndOrderStatusIn(storeId, statuses, pageable);
        return PageResponse.from(page, this::toResponse);
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
        StoreDto store = remoteDataResolver.requireStore(order.getStoreId());
        assertIsStoreOwner(store.getOwnerId());
        order.setOrderStatus(request.getOrderStatus());
        return toResponse(orderRepository.save(order));
    }

    private void assertIsStoreOwner(Long ownerId) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (!currentUserId.equals(ownerId)) {
            throw new AccessDeniedException("You are not the owner of this store.");
        }
    }

    private void assertCanViewOrder(Order order) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (currentUserId.equals(order.getUserId())) {
            return;
        }
        StoreDto store = remoteDataResolver.requireStore(order.getStoreId());
        if (!currentUserId.equals(store.getOwnerId())) {
            throw new AccessDeniedException("You do not have access to this order.");
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

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userFullName(order.getUserFullName())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }
}
