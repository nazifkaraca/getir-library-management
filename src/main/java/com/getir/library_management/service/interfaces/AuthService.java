package com.getir.library_management.service.interfaces;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    UserResponseDto register(RegisterRequestDto request);
    AuthenticationResponseDto login(AuthenticationRequestDto request) throws BadRequestException;
}
