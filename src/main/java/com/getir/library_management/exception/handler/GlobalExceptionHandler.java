package com.getir.library_management.exception.handler;

import com.getir.library_management.exception.ErrorResponse;
import com.getir.library_management.exception.custom.*;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

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

    // Email already exists exception handler
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.CONFLICT.value()) // 404 status code
                .error("Email Already Exists") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // User not found exception handler
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

    // Borrowing not found exception handler
    @ExceptionHandler(BorrowingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBorrowingNotFoundException(BorrowingNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.NOT_FOUND.value()) // 404 status code
                .error("Borrowing Not Found") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Book not found exception handler
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(BookNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.NOT_FOUND.value()) // 404 status code
                .error("Book Not Found") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Book already exists exception handler
    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyExistsException(BookAlreadyExistsException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.CONFLICT.value()) // 404 status code
                .error("Book Already Exists") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Book already exists exception handler
    @ExceptionHandler(BookUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBookUnavailableException(BookUnavailableException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // When the error happened
                .status(HttpStatus.CONFLICT.value()) // 404 status code
                .error("The book is currently borrowed by another user.") // Short title
                .message(ex.getMessage()) // Exception message
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Handles validation errors (e.g., @Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(message)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Parameter")
                .message("Missing required parameter: " + ex.getParameterName())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles unsupported HTTP methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error("Method Not Allowed")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("You do not have permission to access this resource.")
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // Error time
                .status(HttpStatus.BAD_REQUEST.value()) // 400
                .error("Invalid Sort Field") // Human-readable message
                .message("The sort field is invalid or does not exist in Book entity.") // Developer/user hint
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
