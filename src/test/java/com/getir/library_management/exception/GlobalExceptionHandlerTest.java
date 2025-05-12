package com.getir.library_management.exception;

import com.getir.library_management.dto.auth.LoginRequestDto;
import com.getir.library_management.dto.book.CreateBookRequestDto;
import com.getir.library_management.dto.user.RegisterRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.exception.custom.*;
import com.getir.library_management.service.impl.AuthServiceImpl;
import com.getir.library_management.service.impl.BookServiceImpl;
import com.getir.library_management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private AuthServiceImpl authService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @Test
    void shouldHandleBookNotFoundException() throws Exception {
        when(bookService.getBookById(1L)).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/api/book/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book Not Found"))
                .andExpect(jsonPath("$.message").value(ExceptionMessages.BOOK_NOT_FOUND));
    }

    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    @Test
    void shouldHandleBookAlreadyExistsException() throws Exception {
        String validBookJson = """
        {
          "title": "Test Book",
          "author": "Test Author",
          "isbn": "1234567890",
          "genre": "Test Genre",
          "publicationDate": "2025-01-01"
        }
        """;

        when(bookService.addBook(any(CreateBookRequestDto.class)))
                .thenThrow(new BookAlreadyExistsException("Book exists"));

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Book Already Exists"))
                .andExpect(jsonPath("$.message").value(ExceptionMessages.BOOK_EXISTS));
    }

    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    @Test
    void shouldHandleEmailAlreadyExistsException() throws Exception {
        String validRegisterJson = """
        {
          "fullName": "Ali Simsek",
          "email": "ali@example.com",
          "password": "Abcd!1234"
        }
        """;

        when(authService.register(any(RegisterRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email Already Exists"))
                .andExpect(jsonPath("$.message").value("Email already exists."));

    }

    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    @Test
    void shouldHandleUserNotFoundException() throws Exception {
        when(userService.getUserById(1L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User Not Found"))
                .andExpect(jsonPath("$.message").value(ExceptionMessages.USER_NOT_FOUND));
    }

    @WithMockUser
    @Test
    void shouldHandleBadCredentialsException() throws Exception {
        String validLogin = """
        {
          "email": "test@test.com",
          "password": "wrongpassword"
        }
        """;

        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLogin))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @WithMockUser
    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        when(bookService.getBookById(-1L)).thenThrow(new IllegalArgumentException("Invalid sort field"));

        mockMvc.perform(get("/api/book/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Argument"))
                .andExpect(jsonPath("$.message").value("The sort field is invalid or does not exist in Book entity."));
    }

    @WithMockUser
    @Test
    void shouldHandleAccessDeniedException() throws Exception {
        when(bookService.getBookById(2L)).thenThrow(new AccessDeniedException("Access is denied"));

        mockMvc.perform(get("/api/book/2"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."));
    }
}
