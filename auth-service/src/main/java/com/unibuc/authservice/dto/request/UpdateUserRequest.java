package com.unibuc.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Update Current User Profile")
public class UpdateUserRequest {
    @Schema(description = "Full Name", example = "Jane Doe")
    private String fullName;

    @Schema(description = "Phone Number", example = "+40712345678")
    private String phoneNumber;

    @Valid
    @Schema(description = "Delivery address; when provided, replaces the saved address")
    private AddressRequest address;
}
