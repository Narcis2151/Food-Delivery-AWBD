package com.unibuc.storeservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Address Request Details")
public class AddressRequest {
    @NotBlank
    @Schema(description = "Street", example = "123 Main St")
    private String street;

    @NotBlank
    @Schema(description = "City", example = "Bucharest")
    private String city;

    @NotBlank
    @Schema(description = "State", example = "Ilfov")
    private String state;

    @NotBlank
    @Schema(description = "Country", example = "Romania")
    private String country;

    @NotNull
    @Schema(description = "Latitude", example = "44.4268")
    private BigDecimal latitude;

    @NotNull
    @Schema(description = "Longitude", example = "26.1025")
    private BigDecimal longitude;
}
