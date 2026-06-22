package com.unibuc.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Store Request Details")
public class CreateStoreRequest {
    @NotBlank
    @Schema(description = "Store Name", example = "Pizza Palace")
    private String name;

    @NotNull
    @Schema(description = "Owner User ID", example = "1")
    private Long ownerId;

    @Schema(description = "Contact Phone Number", example = "+40712345678")
    private String contactPhoneNumber;

    @Valid
    @Schema(description = "Store address")
    private AddressRequest address;
}
