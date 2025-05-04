package com.getir.library_management.repository;

import com.getir.library_management.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldFindByIsbn() {
        Book book = Book.builder().title("Title").author("Author").isbn("999").build();
        bookRepository.save(book);

        Optional<Book> found = bookRepository.findByIsbn("999");

        assertTrue(found.isPresent());
        assertEquals("999", found.get().getIsbn());
    }

    @Test
    void shouldReturnTrueWhenIsbnExists() {
        bookRepository.save(Book.builder().isbn("check-123").title("abc").author("def").build());
        boolean exists = bookRepository.existsByIsbn("check-123");
        assertTrue(exists);
    }

    @Test
    void shouldSearchBooks() {
        bookRepository.save(Book.builder().title("Clean Code").author("Robert C. Martin").isbn("1234").genre("Software").build());
        bookRepository.save(Book.builder().title("Clean Architecture").author("Robert C. Martin").isbn("5678").genre("Software").build());

        Page<Book> result = bookRepository.searchBooks("clean", null, null, null, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }
}
