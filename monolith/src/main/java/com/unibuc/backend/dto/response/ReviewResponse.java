package com.unibuc.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Review Response Details")
public class ReviewResponse {
    @Schema(description = "Review ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User Full Name", example = "John Doe")
    private String userFullName;

    @Schema(description = "Menu Item ID", example = "1")
    private Long menuItemId;

    @Schema(description = "Menu Item Name", example = "Classic Burger")
    private String menuItemName;

    @Schema(description = "Rating (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "Comment", example = "Great burger!")
    private String comment;

    @Schema(description = "Created At")
    private LocalDateTime createdAt;
}
