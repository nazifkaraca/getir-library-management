package com.getir.library_management.repository;

import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BorrowingRepositoryTest {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldFindByUserId() {
        User user = userRepository.save(User.builder().fullName("Ali").email("ali@test.com").password("123").build());
        Book book = bookRepository.save(Book.builder().title("Kitap").author("Yazar").isbn("123").build());

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .build();
        borrowingRepository.save(borrowing);

        List<Borrowing> result = borrowingRepository.findByUserId(user.getId());

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByBookId() {
        User user = userRepository.save(User.builder().fullName("Veli").email("veli@test.com").password("123").build());
        Book book = bookRepository.save(Book.builder().title("Kitap2").author("Yazar2").isbn("456").build());

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .build();
        borrowingRepository.save(borrowing);

        List<Borrowing> result = borrowingRepository.findByBookId(book.getId());

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindActiveBorrowingByBookId() {
        User user = userRepository.save(User.builder().fullName("Mehmet").email("mehmet@test.com").password("123").build());
        Book book = bookRepository.save(Book.builder().title("Kitap3").author("Yazar3").isbn("789").build());

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .returnDate(null)
                .build();
        borrowingRepository.save(borrowing);

        Borrowing result = borrowingRepository.findByBookIdAndReturnDateIsNull(book.getId());

        assertNotNull(result);
        assertEquals(book.getId(), result.getBook().getId());
    }
}
