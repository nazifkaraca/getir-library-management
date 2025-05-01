package com.getir.library_management.exception.custom;

public class BookUnavailableException extends RuntimeException {

    public BookUnavailableException(String message) {
        super(message); // Pass the message to the parent RuntimeException
    }
}
