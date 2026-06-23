package com.unibuc.orderservice.dto.response;

import com.unibuc.orderservice.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order Response Details")
public class OrderResponse {
    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Customer User ID", example = "1")
    private Long userId;

    @Schema(description = "Customer Full Name", example = "John Doe")
    private String userFullName;

    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @Schema(description = "Store Name", example = "Pizza Palace")
    private String storeName;

    @Schema(description = "Order Status", example = "PENDING")
    private OrderStatus orderStatus;

    @Schema(description = "Total Amount", example = "25.98")
    private BigDecimal totalAmount;

    @Schema(description = "Created At")
    private LocalDateTime createdAt;

    @Schema(description = "Updated At")
    private LocalDateTime updatedAt;

    @Schema(description = "Order Items")
    private List<OrderItemResponse> items;
}
