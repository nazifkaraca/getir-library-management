package com.getir.library_management.dto.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

// This DTO is used to update user information
@Data
@Builder
public class UpdateUserRequestDto {
    @NotBlank(message = "Id cannot be empty.")
    private Long id;
    private String fullName; // New full name
    private String email; // New email address
}
