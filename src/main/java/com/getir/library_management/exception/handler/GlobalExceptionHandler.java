package com.getir.library_management.exception.handler;

import com.getir.library_management.exception.ErrorResponse;
import com.getir.library_management.exception.custom.EmailAlreadyExistsException;
import com.getir.library_management.exception.custom.UserNotFoundException;
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
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // Set HTTP status code
                .error("Internal Server Error") // Human-readable error type
                .message(ex.getMessage()) // Actual exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // Return error response with HTTP status
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.CONFLICT.value()) // 404 status code
                .error("Conflict") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.NOT_FOUND.value()) // 404 status code
                .error("User Not Found") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
