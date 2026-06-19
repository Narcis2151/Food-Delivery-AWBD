package com.unibuc.backend.controller;

import com.unibuc.backend.dto.request.OrderRequest;
import com.unibuc.backend.dto.request.OrderStatusUpdateRequest;
import com.unibuc.backend.dto.response.OrderResponse;
import com.unibuc.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create Order", description = "Place a new order. The authenticated user becomes the customer.")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping("/my")
    @Operation(summary = "Get My Orders", description = "Retrieve all orders placed by the currently authenticated customer.")
    public ResponseEntity<List<OrderResponse>> findMyOrders() {
        return ResponseEntity.ok(orderService.findMyOrders());
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get Store Orders", description = "Retrieve all orders for a store. Only the store owner can perform this action.")
    public ResponseEntity<List<OrderResponse>> findByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(orderService.findByStoreId(storeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order By ID", description = "Retrieve a single order. Accessible by the customer who placed it or the store owner.")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Order Status", description = "Update the status of an order. Only the store owner can perform this action.")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }
}
