package com.unibuc.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Login Details")
public class LoginRequest {

    @NotBlank()
    @Email()
    @Schema(description = "Email Of The User", example = "test.user@mail.com")
    private String email;

    @NotBlank()
    @Schema(description = "Password Of The User", example = "password")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
