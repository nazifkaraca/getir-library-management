package com.getir.library_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class EmailAlreadyExistsHandler {

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
}
