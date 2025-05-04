package com.getir.library_management.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @Schema(
            description = "User's full name",
            example = "John Doe",
            defaultValue = "John Doe"
    )
    @NotBlank(message = "Full name is required.")
    private String fullName;

    @Schema(
            description = "User's email address",
            example = "john.doe@getir.com",
            defaultValue = "john.doe@getir.com"
    )
    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @Schema(
            description = "User's password",
            example = "Secure@123",
            defaultValue = "Secure@123"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    @NotBlank(message = "Password is required.")
    private String password;

}
