package com.getir.library_management.service;

import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.service.impl.OverdueReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OverdueReportServiceImplTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @InjectMocks
    private OverdueReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateOverdueBooksCsv_ShouldReturnCorrectCsvContent() {
        // Arrange
        User user = User.builder().id(1L).fullName("Ali").build();
        Book book = Book.builder().id(1L).title("Kitap").build();
        Borrowing borrowing = Borrowing.builder()
                .id(10L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.of(2024, 5, 1))
                .dueDate(LocalDate.of(2024, 5, 8))
                .returnDate(null)
                .build();

        when(borrowingRepository.findByReturnDateIsNullAndDueDateBefore(any()))
                .thenReturn(List.of(borrowing));

        // Act
        byte[] result = reportService.generateOverdueBooksCsv();
        String csv = new String(result);

        // Assert
        String[] lines = csv.split("\n");
        assertEquals("ID,User,Book,Borrow Date,Due Date", lines[0].trim());
        assertTrue(lines[1].contains("10"));
        assertTrue(lines[1].contains("Ali"));
        assertTrue(lines[1].contains("Kitap"));
        assertTrue(lines[1].contains("2024-05-01"));
        assertTrue(lines[1].contains("2024-05-08"));

        verify(borrowingRepository, times(1))
                .findByReturnDateIsNullAndDueDateBefore(any());
    }
}
