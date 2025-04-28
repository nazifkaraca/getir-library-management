package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.UserService;
import com.getir.library_management.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Register
    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password with BCrypt
                .role(Role.ROLE_USER) // Assign default role of "User"
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    // Login
    @Override
    public AuthenticationResponseDto login(AuthenticationRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

}
