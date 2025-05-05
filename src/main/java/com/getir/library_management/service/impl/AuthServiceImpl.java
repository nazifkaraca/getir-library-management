package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.ErrorMessages;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    // Register
    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            auditLogService.logAction("anonymous", "REGISTER_FAILED", "Email already registered: " + request.getEmail());
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);

        auditLogService.logAction(savedUser.getEmail(), "REGISTER_SUCCESS", "New user registered.");

        return modelMapper.map(savedUser, UserResponseDto.class);
    }


    // Login
    @Override
    public AuthenticationResponseDto login(AuthenticationRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    auditLogService.logAction("anonymous", "LOGIN_FAILED", "Email not found: " + request.getEmail());
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            auditLogService.logAction(user.getEmail(), "LOGIN_FAILED", "Incorrect password.");
            throw new BadCredentialsException("Invalid credentials.");
        }

        String token = jwtService.generateToken(user);

        auditLogService.logAction(user.getEmail(), "LOGIN_SUCCESS", "JWT token issued.");

        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }
}
