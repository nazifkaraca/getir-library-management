package com.getir.library_management.dto.borrow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Used to return borrowing details to the client
@Data
@Builder
public class BorrowResponseDto {
    private Long id;
    private String userFullName;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}
