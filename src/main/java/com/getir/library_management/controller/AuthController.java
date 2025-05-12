package com.getir.library_management.controller;

import com.getir.library_management.dto.auth.LoginRequestDto;
import com.getir.library_management.dto.auth.LoginResponseDto;
import com.getir.library_management.dto.user.RegisterRequestDto;
import com.getir.library_management.dto.user.UserResponseDto;
import com.getir.library_management.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Login endpoint - public
    // POST http://localhost:8070/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) throws BadRequestException {
        return ResponseEntity.ok(authService.login(request));
    }

    // Register endpoint - public
    // POST http://localhost:8070/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }
}
