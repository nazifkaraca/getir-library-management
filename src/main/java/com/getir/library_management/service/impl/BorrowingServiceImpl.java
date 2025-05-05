package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Book.BookAvailabilityDto;
import com.getir.library_management.dto.Borrow.BorrowRequestDto;
import com.getir.library_management.dto.Borrow.BorrowResponseDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.ErrorMessages;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.exception.custom.BookUnavailableException;
import com.getir.library_management.exception.custom.BorrowingNotFoundException;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.logging.audit.CurrentUserService;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.interfaces.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;
    private final BookAvailabilityServiceImpl bookAvailabilityService;

    // Borrow a book for a user if the book is available
    @Override
    public BorrowResponseDto borrowBook(BorrowRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));

        // Check if the book is currently available
        if (!book.isAvailability()) {
            throw new BookUnavailableException(ErrorMessages.BOOK_UNAVAILABLE);
        }

        // Set book availability to false and notify via WebFlux stream
        book.setAvailability(false);
        bookRepository.save(book);

        // Notify subscribers that the book is not available now
        bookAvailabilityService.publishAvailabilityUpdate(
                new BookAvailabilityDto(book.getId(), book.getTitle(), false)
        );

        // Create a new borrowing record
        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7)) // 7-day borrowing period
                .build();

        Borrowing saved = borrowingRepository.save(borrowing);

        // Write an audit log for the borrow action
        auditLogService.logAction(
                currentUserService.getEmail(),
                "BORROW_BOOK",
                "Book ID: " + book.getId() + ", Title: " + book.getTitle()
        );

        // Return response DTO
        return BorrowResponseDto.builder()
                .id(saved.getId())
                .userFullName(user.getFullName())
                .bookTitle(book.getTitle())
                .borrowDate(saved.getBorrowDate())
                .dueDate(saved.getDueDate())
                .returnDate(null)
                .build();
    }

    // Return a borrowed book and mark it as available
    @Override
    public BorrowResponseDto returnBook(Long borrowingId) {
        Borrowing borrowedBook = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BorrowingNotFoundException(ErrorMessages.BORROWING_NOT_FOUND));

        Book book = borrowedBook.getBook();
        book.setAvailability(true);
        bookRepository.save(book);

        // Notify subscribers that the book is now available
        bookAvailabilityService.publishAvailabilityUpdate(
                new BookAvailabilityDto(book.getId(), book.getTitle(), true)
        );

        borrowedBook.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowedBook);

        // Write an audit log for the return action
        auditLogService.logAction(
                currentUserService.getEmail(),
                "RETURN_BOOK",
                "Book ID: " + book.getId() + ", Title: " + book.getTitle()
        );

        // Return response DTO
        return BorrowResponseDto.builder()
                .id(borrowedBook.getId())
                .userFullName(borrowedBook.getUser().getFullName())
                .bookTitle(book.getTitle())
                .borrowDate(borrowedBook.getBorrowDate())
                .dueDate(borrowedBook.getDueDate())
                .returnDate(borrowedBook.getReturnDate())
                .build();
    }

    // Retrieve all borrow records
    @Override
    public List<BorrowResponseDto> getAllBorrowings() {
        return borrowingRepository.findAll().stream()
                .map(borrowing -> BorrowResponseDto.builder()
                        .id(borrowing.getId())
                        .userFullName(borrowing.getUser().getFullName())
                        .bookTitle(borrowing.getBook().getTitle())
                        .borrowDate(borrowing.getBorrowDate())
                        .dueDate(borrowing.getDueDate())
                        .returnDate(borrowing.getReturnDate())
                        .build()
                ).toList();
    }

    // Retrieve all borrowings for a specific user
    @Override
    public List<BorrowResponseDto> getBorrowingsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        return borrowingRepository.findByUserId(userId).stream()
                .map(borrowing -> BorrowResponseDto.builder()
                        .id(borrowing.getId())
                        .userFullName(borrowing.getUser().getFullName())
                        .bookTitle(borrowing.getBook().getTitle())
                        .borrowDate(borrowing.getBorrowDate())
                        .dueDate(borrowing.getDueDate())
                        .returnDate(borrowing.getReturnDate())
                        .build()
                ).toList();
    }

    // Retrieve list of overdue books (not returned and past due date)
    @Override
    public List<BorrowResponseDto> getOverdueBooks() {
        LocalDate today = LocalDate.now();

        return borrowingRepository.findAll().stream()
                .filter(b -> b.getReturnDate() == null && b.getDueDate().isBefore(today))
                .map(b -> BorrowResponseDto.builder()
                        .id(b.getId())
                        .userFullName(b.getUser().getFullName())
                        .bookTitle(b.getBook().getTitle())
                        .borrowDate(b.getBorrowDate())
                        .dueDate(b.getDueDate())
                        .returnDate(null)
                        .build()
                ).toList();
    }
}
