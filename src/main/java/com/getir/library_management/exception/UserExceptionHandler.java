package com.getir.library_management.exception;

import com.getir.library_management.entity.User;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UserExceptionHandler {

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
