package com.getir.library_management.dto.Auth;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
