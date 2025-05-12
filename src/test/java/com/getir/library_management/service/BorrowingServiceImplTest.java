package com.getir.library_management.service;

import com.getir.library_management.dto.borrow.BorrowRequestDto;
import com.getir.library_management.dto.borrow.BorrowResponseDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.exception.custom.BookUnavailableException;
import com.getir.library_management.exception.custom.BorrowingNotFoundException;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.logging.audit.CurrentUserService;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.impl.BookAvailabilityServiceImpl;
import com.getir.library_management.service.impl.BorrowingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class BorrowingServiceImplTest {

    @Mock
    private BorrowingRepository borrowingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookAvailabilityServiceImpl bookAvailabilityService;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void borrowBook_ShouldSucceed() {
        BorrowRequestDto request = new BorrowRequestDto();
        request.setUserId(1L);
        request.setBookId(2L);

        User user = User.builder().id(1L).fullName("Ali").build();
        Book book = Book.builder().id(2L).title("Kitap").availability(true).build();
        Borrowing saved = Borrowing.builder().id(3L).user(user).book(book).borrowDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(7)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenReturn(book);
        when(borrowingRepository.save(any())).thenReturn(saved);

        BorrowResponseDto response = borrowingService.borrowBook(request);

        assertEquals("Kitap", response.getBookTitle());
        assertEquals("Ali", response.getUserFullName());
    }

    @Test
    void borrowBook_ShouldThrow_WhenUserNotFound() {
        BorrowRequestDto request = new BorrowRequestDto();
        request.setUserId(1L);
        request.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> borrowingService.borrowBook(request));
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookNotFound() {
        BorrowRequestDto request = new BorrowRequestDto();
        request.setUserId(1L);
        request.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> borrowingService.borrowBook(request));
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookUnavailable() {
        BorrowRequestDto request = new BorrowRequestDto();
        request.setUserId(1L);
        request.setBookId(2L);

        User user = new User();
        Book book = Book.builder().availability(false).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        assertThrows(BookUnavailableException.class, () -> borrowingService.borrowBook(request));
    }

    @Test
    void returnBook_ShouldSucceed() {
        User user = User.builder().fullName("Ali").build();
        Book book = Book.builder().title("Kitap").availability(false).build();
        Borrowing borrowing = Borrowing.builder().id(1L).user(user).book(book).borrowDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(7)).build();

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(bookRepository.save(any())).thenReturn(book);
        when(borrowingRepository.save(any())).thenReturn(borrowing);

        BorrowResponseDto response = borrowingService.returnBook(1L);

        assertEquals("Kitap", response.getBookTitle());
        assertNotNull(response.getReturnDate());
    }

    @Test
    void returnBook_ShouldThrow_WhenNotFound() {
        when(borrowingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BorrowingNotFoundException.class, () -> borrowingService.returnBook(99L));
    }

    @Test
    void getAllBorrowings_ShouldReturnList() {
        User user = User.builder().fullName("Ali").build();
        Book book = Book.builder().title("Kitap").build();
        Borrowing b = Borrowing.builder().id(1L).user(user).book(book).borrowDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(7)).build();

        when(borrowingRepository.findAll()).thenReturn(List.of(b));

        List<BorrowResponseDto> result = borrowingService.getAllBorrowings();

        assertEquals(1, result.size());
        assertEquals("Kitap", result.get(0).getBookTitle());
    }

    @Test
    void getBorrowingsByUser_ShouldReturnList() {
        User user = User.builder().id(1L).fullName("Ali").build();
        Book book = Book.builder().title("Kitap").build();
        Borrowing b = Borrowing.builder().id(1L).user(user).book(book).borrowDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(7)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(borrowingRepository.findByUserId(1L)).thenReturn(List.of(b));

        List<BorrowResponseDto> result = borrowingService.getBorrowingsByUser(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getOverdueBooks_ShouldReturnList() {
        User user = User.builder().fullName("Ali").build();
        Book book = Book.builder().title("Kitap").build();
        Borrowing overdue = Borrowing.builder()
                .id(1L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        when(borrowingRepository.findByReturnDateIsNullAndDueDateBefore(any()))
                .thenReturn(List.of(overdue));

        List<BorrowResponseDto> result = borrowingService.getOverdueBooks();

        assertEquals(1, result.size());
    }
}