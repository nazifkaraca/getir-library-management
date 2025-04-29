package com.getir.library_management.dto.User;

import com.getir.library_management.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequestDto {

    @Schema(
            description = "User's ID",
            example = "1",
            defaultValue = "1"
    )
    @NotBlank(message = "Id cannot be empty.")
    private Long id;

    @Schema(
            description = "Updated full name",
            example = "Updated Name",
            defaultValue = "Updated Name"
    )
    private String fullName;

    @Schema(
            description = "Updated email address",
            example = "updated.user@getir.com",
            defaultValue = "updated.user@getir.com"
    )
    private String email;

    @Schema(
            description = "Updated role of the user",
            example = "ROLE_USER",
            defaultValue = "ROLE_USER"
    )
    private Role role;
}
