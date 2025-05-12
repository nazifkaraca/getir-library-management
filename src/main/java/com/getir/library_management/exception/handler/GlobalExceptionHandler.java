package com.getir.library_management.exception.handler;

import com.getir.library_management.exception.ExceptionMessages;
import com.getir.library_management.exception.ErrorResponse;
import com.getir.library_management.exception.custom.*;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Helper method to build consistent ErrorResponse objects
    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
        log.warn("Book not found: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, "Book Not Found", ExceptionMessages.BOOK_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyExists(BookAlreadyExistsException ex) {
        log.warn("Book already exists: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, "Book Already Exists", ExceptionMessages.BOOK_EXISTS), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, "User Not Found", ExceptionMessages.USER_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBookUnavailable(BookUnavailableException ex) {
        log.warn("Book unavailable: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, "Book Unavailable", ExceptionMessages.BOOK_UNAVAILABLE), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BorrowingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBorrowingNotFound(BorrowingNotFoundException ex) {
        log.warn("Borrowing not found: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, "Borrowing Not Found", ExceptionMessages.BORROWING_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.info("Invalid login attempt.");
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password."), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.info("Email already exists: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, "Email Already Exists", "Email already exists."), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException ex) {
        log.warn("JWT error: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or Expired Token", "Authentication token is invalid or has expired."), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, "Data Integrity Violation", "This resource is still referenced by another entity and cannot be deleted."), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.warn("Validation failed: {}", message);
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = "Missing required parameter: " + ex.getParameterName();
        log.warn("Missing parameter: {}", ex.getParameterName());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing Parameter", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not allowed: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to access this resource."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Argument", "The sort field is invalid or does not exist in Book entity."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        log.error("Unhandled runtime exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
