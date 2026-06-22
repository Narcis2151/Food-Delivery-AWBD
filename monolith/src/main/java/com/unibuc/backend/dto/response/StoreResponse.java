package com.unibuc.backend.dto.response;

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
@Schema(description = "Store Response Details")
public class StoreResponse {
    @Schema(description = "Store ID", example = "1")
    private Long id;

    @Schema(description = "Store Name", example = "Pizza Palace")
    private String name;

    @Schema(description = "Address")
    private AddressResponse address;

    @Schema(description = "Contact Phone Number", example = "+40712345678")
    private String contactPhoneNumber;

    @Schema(description = "Owner Email", example = "store_owner@mail.com")
    private String ownerEmail;
}
