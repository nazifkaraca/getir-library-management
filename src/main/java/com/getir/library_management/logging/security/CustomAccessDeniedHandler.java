package com.getir.library_management.logging.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // Handles access denied exceptions for authenticated users lacking required permissions
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Log a warning with the request path and the exception message
        log.warn("ACCESS DENIED | Path: {} | Message: {}", request.getRequestURI(), accessDeniedException.getMessage());

        // Build a custom error response object with relevant details
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("You are authenticated but not authorized to access this resource.")
                .build();

        // Set response content type and status, then write the error as JSON to the response
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}

