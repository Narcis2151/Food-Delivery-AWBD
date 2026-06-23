package com.unibuc.orderservice.service;

import com.unibuc.orderservice.dto.request.OrderRequest;
import com.unibuc.orderservice.dto.request.OrderStatusUpdateRequest;
import com.unibuc.orderservice.dto.response.OrderResponse;
import com.unibuc.orderservice.dto.response.PageResponse;
import com.unibuc.orderservice.model.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    PageResponse<OrderResponse> findMyOrders(List<OrderStatus> statuses, Pageable pageable);
    PageResponse<OrderResponse> findByStoreId(Long storeId, List<OrderStatus> statuses, Pageable pageable);
    OrderResponse findById(Long id);
    OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request);
}
