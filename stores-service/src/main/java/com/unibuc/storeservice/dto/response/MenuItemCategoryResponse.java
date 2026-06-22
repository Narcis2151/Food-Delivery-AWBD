package com.unibuc.storeservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Menu Item Category Response Details")
public class MenuItemCategoryResponse {
    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @Schema(description = "Store Name", example = "Pizza Palace")
    private String storeName;

    @Schema(description = "Category Name", example = "Burgers")
    private String name;

    @Schema(description = "Display Order", example = "1")
    private Integer displayOrder;
}
