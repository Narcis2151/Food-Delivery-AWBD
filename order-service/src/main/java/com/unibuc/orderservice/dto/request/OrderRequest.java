package com.unibuc.orderservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Request Details")
public class OrderRequest {
    @NotNull
    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @Valid
    @NotEmpty
    @Schema(description = "List of items to order")
    private List<OrderItemRequest> items;
}
