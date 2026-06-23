package com.unibuc.authservice.dto.response;

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
@Schema(description = "Address Response Details")
public class AddressResponse {
    @Schema(description = "Address ID", example = "1")
    private Long id;

    @Schema(description = "Street", example = "123 Main St")
    private String street;

    @Schema(description = "City", example = "Bucharest")
    private String city;

    @Schema(description = "State", example = "Ilfov")
    private String state;

    @Schema(description = "Country", example = "Romania")
    private String country;

    @Schema(description = "Latitude", example = "44.4268")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "26.1025")
    private BigDecimal longitude;
}
