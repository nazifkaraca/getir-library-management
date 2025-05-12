package com.getir.library_management.exception;

public class ErrorMessages {
    public static final String BOOK_NOT_FOUND = "The requested book could not be found in the system. Please check the book ID or try a different search term.";
    public static final String BOOK_EXISTS = "A book with the same ISBN already exists in the library. Duplicate entries are not allowed.";
    public static final String USER_NOT_FOUND = "The specified user could not be located. Please ensure the user ID or email is correct.";
    public static final String BOOK_UNAVAILABLE = "The book you are trying to borrow is currently not available. It may have been borrowed by another user.";
    public static final String BORROWING_NOT_FOUND = "The borrowing record you are looking for does not exist or has already been returned.";
}
