package com.getir.library_management.exception.custom;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message); // Pass the message to the parent RuntimeException
    }
}
