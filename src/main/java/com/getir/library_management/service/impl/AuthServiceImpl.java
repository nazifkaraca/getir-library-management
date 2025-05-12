package com.getir.library_management.service.impl;

import com.getir.library_management.dto.auth.LoginRequestDto;
import com.getir.library_management.dto.auth.LoginResponseDto;
import com.getir.library_management.dto.user.RegisterRequestDto;
import com.getir.library_management.dto.user.UserResponseDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.ExceptionMessages;
import com.getir.library_management.exception.custom.EmailAlreadyExistsException;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.interfaces.AuthService;
import com.getir.library_management.util.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Repositories and services used for authentication and registration
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    // Handles user registration
    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        // Check if the email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Log failed registration attempt
            auditLogService.logAction("anonymous", "REGISTER_FAILED", "Email already registered: " + request.getEmail());
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        // Create a new user entity
        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        // Save the user to the database
        User savedUser = userRepository.save(newUser);

        // Log successful registration
        auditLogService.logAction(savedUser.getEmail(), "REGISTER_SUCCESS", "New user registered.");

        // Map the saved user to a response DTO and return it
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    // Handles user login and JWT token generation
    @Override
    public LoginResponseDto login(LoginRequestDto request)  {
        // Retrieve user by email or throw exception if not found
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    // Log failed login attempt
                    auditLogService.logAction("anonymous", "LOGIN_FAILED", "Email not found: " + request.getEmail());
                    return new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND);
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log failed login due to incorrect password
            auditLogService.logAction(user.getEmail(), "LOGIN_FAILED", "Incorrect password.");
            throw new BadCredentialsException("Invalid credentials.");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Log successful login
        auditLogService.logAction(user.getEmail(), "LOGIN_SUCCESS", "JWT token issued.");

        // Return authentication response with token
        return LoginResponseDto.builder()
                .token(token)
                .build();
    }
}
