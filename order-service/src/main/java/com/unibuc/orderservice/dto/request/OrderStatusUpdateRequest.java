package com.unibuc.orderservice.dto.request;

import com.unibuc.orderservice.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Status Update Request")
public class OrderStatusUpdateRequest {
    @NotNull
    @Schema(description = "New order status", example = "PREPARING")
    private OrderStatus orderStatus;
}
