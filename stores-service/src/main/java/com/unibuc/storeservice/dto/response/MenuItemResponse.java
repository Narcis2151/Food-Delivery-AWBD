package com.unibuc.storeservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Menu Item Response Details")
public class MenuItemResponse {
    @Schema(description = "Menu Item ID", example = "1")
    private Long id;

    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @Schema(description = "Store Name", example = "Pizza Palace")
    private String storeName;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category Name", example = "Burgers")
    private String categoryName;

    @Schema(description = "Item Name", example = "Classic Burger")
    private String name;

    @Schema(description = "Item Description", example = "Juicy beef patty with lettuce and tomato")
    private String description;

    @Schema(description = "Price", example = "12.99")
    private BigDecimal price;

    @Schema(description = "Is Available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Image URL", example = "https://example.com/burger.jpg")
    private String imageUrl;
}
