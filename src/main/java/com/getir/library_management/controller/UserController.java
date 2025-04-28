package com.getir.library_management.controller;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterRequestDto request) {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
