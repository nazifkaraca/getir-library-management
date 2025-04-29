package com.getir.library_management.exception;

import lombok.val;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

// This class handles all exceptions globally in the application
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle RuntimeException and send a standard error response
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // Capture current time
                .status(HttpStatus.BAD_REQUEST.value()) // Set HTTP status code
                .error("Bad Request") // Human-readable error type
                .message(ex.getMessage()) // Actual exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // Return error response with HTTP status
    }
}
