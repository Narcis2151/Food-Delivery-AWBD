package com.unibuc.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login Response Details")
public class LoginResponse {
    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYxNjMwNjQwMH0.1")
    private String token;

    @Schema(description = "Token Expiry Time", example = "3600")
    private long expiresIn;

}
