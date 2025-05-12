package com.getir.library_management.service.interfaces;

import com.getir.library_management.dto.auth.LoginRequestDto;
import com.getir.library_management.dto.auth.LoginResponseDto;
import com.getir.library_management.dto.user.RegisterRequestDto;
import com.getir.library_management.dto.user.UserResponseDto;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    UserResponseDto register(RegisterRequestDto request);
    LoginResponseDto login(LoginRequestDto request) throws BadRequestException;
}
