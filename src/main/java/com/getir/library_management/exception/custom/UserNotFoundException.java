package com.getir.library_management.exception.custom;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message); // Pass the message to the parent RuntimeException
    }
}
