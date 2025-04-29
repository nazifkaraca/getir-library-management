package com.getir.library_management.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

// This class defines the structure of error responses
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp; // Time when the error occurred
    private int status; // HTTP status code
    private String error; // Error type
    private String message; // Detailed error message
}
