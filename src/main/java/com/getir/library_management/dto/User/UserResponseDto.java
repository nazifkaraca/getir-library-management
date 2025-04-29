package com.getir.library_management.dto.User;

import com.getir.library_management.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
}
