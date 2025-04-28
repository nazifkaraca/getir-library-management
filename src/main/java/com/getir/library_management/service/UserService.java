package com.getir.library_management.service;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;

public interface UserService {
    UserResponseDto register(RegisterRequestDto request);
    AuthenticationResponseDto login(AuthenticationRequestDto request);
}
