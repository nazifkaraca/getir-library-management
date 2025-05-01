package com.getir.library_management.service.interfaces;


import com.getir.library_management.dto.Borrow.BorrowRequestDto;
import com.getir.library_management.dto.Borrow.BorrowResponseDto;

import java.util.List;

public interface BorrowingService {

    BorrowResponseDto borrowBook(BorrowRequestDto request); // Borrow a book
    BorrowResponseDto returnBook(Long borrowingId); // Return a borrowed book
    List<BorrowResponseDto> getAllBorrowings(); // Get all borrowings
    List<BorrowResponseDto> getBorrowingsByUser(Long userId); // View user's borrowing history
    List<BorrowResponseDto> getOverdueBooks(); // Get all overdue borrowings
}
