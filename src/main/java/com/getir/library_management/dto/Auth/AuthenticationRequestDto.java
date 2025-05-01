package com.getir.library_management.dto.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthenticationRequestDto {

    @Schema(
            description = "User's email address",
            example = "user@getir.com",
            defaultValue = "user@getir.com"
    )
    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @Schema(
            description = "User's password",
            example = "Secure@123",
            defaultValue = "Secure@123"
    )

    @NotBlank(message = "Password is required.")
    private String password;

}
