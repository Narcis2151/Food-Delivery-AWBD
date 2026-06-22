package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.OrderRequest;
import com.unibuc.backend.dto.request.OrderStatusUpdateRequest;
import com.unibuc.backend.dto.response.OrderResponse;
import com.unibuc.backend.dto.response.PageResponse;
import com.unibuc.backend.model.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    PageResponse<OrderResponse> findMyOrders(List<OrderStatus> statuses, Pageable pageable);
    PageResponse<OrderResponse> findByStoreId(Long storeId, List<OrderStatus> statuses, Pageable pageable);
    OrderResponse findById(Long id);
    OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request);
}
