package com.getir.library_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;
}
