package com.getir.library_management.repository;

import com.getir.library_management.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    // List all borrowings by user
    List<Borrowing> findByUserId(Long userId);

    // List borrowings by book
    List<Borrowing> findByBookId(Long bookId);

    // Find active borrowing (not yet returned)
    Borrowing findByBookIdAndReturnDateIsNull(Long bookId);
}
