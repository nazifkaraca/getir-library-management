package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.EmailAlreadyExistsException;
import com.getir.library_management.exception.UserNotFoundException;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.UserService;
import com.getir.library_management.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Register
    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        // Check if the email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        // Create new user
        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password with BCrypt
                .role(Role.ROLE_USER) // Assign default role of "User"
                .build();

        // Save new user
        User savedUser = userRepository.save(newUser);

        // Return user response DTO
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
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

}
