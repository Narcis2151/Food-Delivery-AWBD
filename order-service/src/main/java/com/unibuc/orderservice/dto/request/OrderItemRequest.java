package com.unibuc.orderservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Item Request Details")
public class OrderItemRequest {
    @NotNull
    @Schema(description = "Menu Item ID", example = "1")
    private Long menuItemId;

    @NotNull
    @Min(1)
    @Schema(description = "Quantity", example = "2")
    private Integer quantity;
}
