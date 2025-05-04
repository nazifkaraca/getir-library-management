package com.getir.library_management.service;

import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.Auth.AuthenticationResponseDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.custom.EmailAlreadyExistsException;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.impl.AuthServiceImpl;
import com.getir.library_management.util.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldRegisterUserSuccessfully() {
        // Arrange
        RegisterRequestDto request = RegisterRequestDto.builder()
                .fullName("John Doe")
                .email("john.doe@getir.com")
                .password("Secure@123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setEmail("john.doe@getir.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDto.class)).thenReturn(expectedResponse);

        // Act
        UserResponseDto response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.getEmail(), response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("existing@getir.com")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("john.doe@getir.com");
        request.setPassword("Secure@123");

        User user = User.builder()
                .email("john.doe@getir.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act
        AuthenticationResponseDto response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("nonexistent@getir.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIncorrect() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("john.doe@getir.com");
        request.setPassword("wrongpass");

        User user = User.builder()
                .email("john.doe@getir.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
