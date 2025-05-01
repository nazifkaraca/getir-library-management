package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Book.BookResponseDto;
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
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.interfaces.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // Borrow a book
    @Override
    public BorrowResponseDto borrowBook(BorrowRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));

        if (!book.isAvailable()) {
            throw new BookUnavailableException(ErrorMessages.BOOK_UNAVAILABLE);
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7)) // 7-day borrowing period
                .build();

        Borrowing saved = borrowingRepository.save(borrowing);

        return BorrowResponseDto.builder()
                .id(saved.getId())
                .userFullName(user.getFullName())
                .bookTitle(book.getTitle())
                .borrowDate(saved.getBorrowDate())
                .dueDate(saved.getDueDate())
                .returnDate(null)
                .build();
    }

    // Return borrowed book
    @Override
    public BorrowResponseDto returnBook(Long borrowingId) {
        Borrowing borrowedBook = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BorrowingNotFoundException(ErrorMessages.BORROWING_NOT_FOUND));

        Book book = borrowedBook.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        borrowedBook.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowedBook);

        return BorrowResponseDto.builder()
                .id(borrowedBook.getId())
                .userFullName(borrowedBook.getUser().getFullName())
                .bookTitle(book.getTitle())
                .borrowDate(borrowedBook.getBorrowDate())
                .dueDate(borrowedBook.getDueDate())
                .returnDate(borrowedBook.getReturnDate())
                .build();
    }

    // Get all borrowed books
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

    // View all borrowings of a user
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
