package com.unibuc.reviewservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Review Request Details")
public class ReviewRequest {
    @NotNull
    @Schema(description = "Menu Item ID", example = "1")
    private Long menuItemId;

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Rating (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "Comment", example = "Great burger!")
    private String comment;
}
