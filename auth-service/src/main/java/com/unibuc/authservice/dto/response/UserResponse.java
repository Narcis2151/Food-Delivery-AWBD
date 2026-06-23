package com.unibuc.authservice.dto.response;

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
@Schema(description = "Authenticated User Details")
public class UserResponse {
    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Full Name", example = "Jane Doe")
    private String fullName;

    @Schema(description = "Email", example = "jane@example.com")
    private String email;

    @Schema(description = "Role", example = "ROLE_CUSTOMER")
    private String role;

    @Schema(description = "Phone Number", example = "+40712345678")
    private String phoneNumber;

    @Schema(description = "Saved delivery address")
    private AddressResponse address;
}
