package com.unibuc.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Menu Item Request Details")
public class MenuItemRequest {
    @NotNull
    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @NotNull
    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @NotBlank
    @Schema(description = "Item Name", example = "Classic Burger")
    private String name;

    @Schema(description = "Item Description", example = "Juicy beef patty with lettuce and tomato")
    private String description;

    @NotNull
    @DecimalMin("0.01")
    @Schema(description = "Price", example = "12.99")
    private BigDecimal price;

    @NotNull
    @Schema(description = "Is Available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Image URL", example = "https://example.com/burger.jpg")
    private String imageUrl;
}
