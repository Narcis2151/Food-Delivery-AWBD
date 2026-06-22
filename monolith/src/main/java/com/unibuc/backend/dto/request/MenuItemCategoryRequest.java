package com.unibuc.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Menu Item Category Request Details")
public class MenuItemCategoryRequest {
    @NotNull
    @Schema(description = "Store ID", example = "1")
    private Long storeId;

    @NotBlank
    @Schema(description = "Category Name", example = "Burgers")
    private String name;

    @NotNull
    @Min(0)
    @Schema(description = "Display Order", example = "1")
    private Integer displayOrder;
}
