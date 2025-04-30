package com.getir.library_management.exception.custom;

public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String message) {
        super(message); // Pass the message to the parent RuntimeException
    }
}
