package com.getir.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.dto.user.UpdateUserRequestDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .fullName("Initial User")
                .email(UUID.randomUUID() + "@getir.com")
                .password("1234!Abcd")
                .role(Role.ROLE_USER)
                .markedAsDeleted(false)
                .build();

        userId = userRepository.save(user).getId();
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldGetAllUsersAsLibrarian() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldUpdateUserSuccessfully() throws Exception {
        UpdateUserRequestDto update = UpdateUserRequestDto.builder()
                .id(userId)
                .fullName("Updated Name")
                .email("getir@getir.com")
                .build();

        mockMvc.perform(put("/api/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("getir@getir.com"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldSoftDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/user/soft-delete/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldHardDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/user/hard-delete/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectUnauthenticatedUserAccessToAllUsers() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isUnauthorized());
    }
}
