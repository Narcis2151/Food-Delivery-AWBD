package com.unibuc.orderservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order Item Response Details")
public class OrderItemResponse {
    @Schema(description = "Order Item ID", example = "1")
    private Long id;

    @Schema(description = "Menu Item ID", example = "1")
    private Long menuItemId;

    @Schema(description = "Menu Item Name", example = "Classic Burger")
    private String menuItemName;

    @Schema(description = "Quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price at time of order", example = "12.99")
    private BigDecimal price;
}
