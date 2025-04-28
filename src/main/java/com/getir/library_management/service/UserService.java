package com.getir.library_management.service;

import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;

public interface UserService {
    UserResponseDto register(RegisterRequestDto request);
}
