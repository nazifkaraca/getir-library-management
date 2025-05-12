package com.getir.library_management.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Used when a user borrows a book
@Data
public class BorrowRequestDto {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotNull(message = "Book ID is required.")
    private Long bookId;
}
