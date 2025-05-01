package com.getir.library_management.exception.custom;

public class BorrowingNotFoundException extends RuntimeException {

    public BorrowingNotFoundException(String message) {
        super(message); // Pass the message to the parent RuntimeException
    }
}
