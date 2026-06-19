package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.OrderRequest;
import com.unibuc.backend.dto.request.OrderStatusUpdateRequest;
import com.unibuc.backend.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    List<OrderResponse> findMyOrders();
    List<OrderResponse> findByStoreId(Long storeId);
    OrderResponse findById(Long id);
    OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request);
}
