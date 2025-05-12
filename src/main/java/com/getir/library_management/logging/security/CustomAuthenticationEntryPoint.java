package com.getir.library_management.logging.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Used to convert the error response object to JSON
    private final ObjectMapper objectMapper;

    // Handles unauthorized access attempts by unauthenticated users
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log a warning with the request path and the exception message
        log.warn("UNAUTHORIZED | Path: {} | Message: {}", request.getRequestURI(), authException.getMessage());

        // Build a custom error response object with relevant details
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Authentication is required to access this resource.")
                .build();

        // Set response content type and status, then write the error as JSON to the response
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
