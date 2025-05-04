package com.getir.library_management.controller;

import com.getir.library_management.dto.Borrow.BorrowRequestDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private Long bookId;

    @BeforeEach
    void setUp() {
        // Delete all mock variable
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .fullName("Jane Tester")
                .email(UUID.randomUUID() + "getir.com")
                .password("1234")
                .role(Role.ROLE_USER)
                .markedAsDeleted(false)
                .build();
        userId = userRepository.save(user).getId();

        Book book = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn(UUID.randomUUID().toString())
                .availability(true)
                .build();
        bookId = bookRepository.save(book).getId();
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldBorrowBookSuccessfully() throws Exception {
        BorrowRequestDto request = new BorrowRequestDto();
        request.setUserId(userId);
        request.setBookId(bookId);

        mockMvc.perform(post("/api/borrowing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").exists());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldGetBorrowingHistory() throws Exception {
        mockMvc.perform(get("/api/borrowing/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnBookSuccessfully() throws Exception {
        // given
        Long borrowId = borrowingRepository.save(Borrowing.builder()
                .user(userRepository.save(User.builder()
                        .fullName("Ali User")
                        .email(UUID.randomUUID() + "@getir.com")
                        .password("1234")
                        .role(Role.ROLE_USER)
                        .build()))
                .book(bookRepository.save(Book.builder()
                        .title("Test Book")
                        .isbn(UUID.randomUUID().toString())
                        .author("Author")
                        .genre("Tech")
                        .publicationDate("2020")
                        .availability(false)
                        .build()))
                .borrowDate(LocalDate.now())
                .build()).getId();

        // when & then
        mockMvc.perform(put("/api/borrowing/return/{id}", borrowId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllBorrowingsOfUser() throws Exception {
        User user = userRepository.save(User.builder()
                .fullName("Fatma Getir")
                .email(UUID.randomUUID() + "@getir.com")
                .password("1234")
                .role(Role.ROLE_USER)
                .build());

        Book book = bookRepository.save(Book.builder()
                .title("Microservices")
                .isbn(UUID.randomUUID().toString())
                .author("Sam Newman")
                .genre("Software")
                .publicationDate("2019")
                .availability(false)
                .build());

        borrowingRepository.save(Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .build());

        mockMvc.perform(get("/api/borrowing/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Microservices"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldGetOverdueBooks_whenLibrarian() throws Exception {
        User user = userRepository.save(User.builder()
                .fullName("Overdue User")
                .email(UUID.randomUUID() + "@getir.com")
                .password("pass")
                .role(Role.ROLE_USER)
                .build());

        Book book = bookRepository.save(Book.builder()
                .title("Clean Architecture")
                .author("Uncle Bob")
                .isbn(UUID.randomUUID().toString())
                .genre("Software")
                .publicationDate("2017")
                .availability(false)
                .build());

        borrowingRepository.save(Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now().minusDays(15)) // 14 günü geçmiş
                .dueDate(LocalDate.now().minusDays(7))
                .build());

        mockMvc.perform(get("/api/borrowing/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Architecture"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void shouldGetAllBorrowings_whenLibrarian() throws Exception {
        User user = userRepository.save(User.builder()
                .fullName("Getir Admin")
                .email(UUID.randomUUID() + "@getir.com")
                .password("pass")
                .role(Role.ROLE_USER)
                .build());

        Book book = bookRepository.save(Book.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .isbn(UUID.randomUUID().toString())
                .genre("Software")
                .publicationDate("1999")
                .availability(false)
                .build());

        borrowingRepository.save(Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .build());

        mockMvc.perform(get("/api/borrowing/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Refactoring"));
    }

    @Test
    void shouldRejectUnauthorizedAccessToBorrowings() throws Exception {
        mockMvc.perform(get("/api/borrowing/all"))
                .andExpect(status().isUnauthorized());
    }
}
