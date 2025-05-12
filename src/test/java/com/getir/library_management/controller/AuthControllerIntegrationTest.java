package com.getir.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.dto.Auth.AuthenticationRequestDto;
import com.getir.library_management.dto.User.RegisterRequestDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.exception.ErrorMessages;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @BeforeEach
    void cleanDatabase() {
        borrowingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .fullName("New User")
                .email("newuser@getir.com")
                .password("1234!Abcd")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        userRepository.save(com.getir.library_management.entity.User.builder()
                .fullName("Existing")
                .email("duplicate@getir.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.ROLE_USER)
                .build());

        RegisterRequestDto request = RegisterRequestDto.builder()
                .fullName("Duplicate")
                .email("duplicate@getir.com")
                .password("1234!Abcd")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email Already Exists"))
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        userRepository.save(com.getir.library_management.entity.User.builder()
                .fullName("Login User")
                .email("login@getir.com")
                .password(passwordEncoder.encode("1234!Abcd"))
                .role(Role.ROLE_USER)
                .build());

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("login@getir.com");
        request.setPassword("1234!Abcd");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidLogin() throws Exception {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("wrong@getir.com");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User Not Found"))
                .andExpect(jsonPath("$.message").value("The specified user could not be located. Please ensure the user ID or email is correct."));
    }
}
