package com.unibuc.backend.dto.request;

import com.unibuc.backend.model.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registration Details")
public class RegisterRequest {
    @NotBlank()
    @Schema(description = "Full Name Of The User", example = "Test User")
    private String fullName;

    @NotBlank()
    @Email()
    @Schema(description = "Email Of The User", example = "test.user@mail.com")
    private String email;

    @NotBlank()
    @Schema(description = "Password Of The User", example = "password")
    private String password;

//    @NotBlank()
    @Schema(description = "Role Of The User", example = "USER")
    private ERole role;


}
