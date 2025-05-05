package com.getir.library_management.service.interfaces;

import com.getir.library_management.dto.Book.BookResponseDto;
import com.getir.library_management.dto.Book.CreateBookRequestDto;
import com.getir.library_management.dto.Book.UpdateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookResponseDto addBook(CreateBookRequestDto request);

    BookResponseDto getBookById(Long id);

    Page<BookResponseDto> searchBooks(String title, String author, String isbn, String genre, Pageable pageable);

    BookResponseDto updateBook(Long id, UpdateBookRequestDto request);

    void deleteBook(Long id);

    List<BookResponseDto> getAllBooks();
}
