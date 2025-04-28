package com.getir.library_management.service;

import com.getir.library_management.dto.RegisterRequestDto;
import com.getir.library_management.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(RegisterRequestDto request);
}
