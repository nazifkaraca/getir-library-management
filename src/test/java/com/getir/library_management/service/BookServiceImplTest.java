package com.getir.library_management.service;

import com.getir.library_management.dto.Book.BookResponseDto;
import com.getir.library_management.dto.Book.CreateBookRequestDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookRepository bookRepository;
    private ModelMapper modelMapper;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        modelMapper = mock(ModelMapper.class);
        bookService = new BookServiceImpl(bookRepository, modelMapper);
    }

    @Test
    void shouldAddBookSuccessfully() {
        // Arrange
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setIsbn("123");

        Book book = Book.builder().title("Test Book").author("Test Author").isbn("123").build();
        Book savedBook = Book.builder().id(1L).title("Test Book").author("Test Author").isbn("123").build();
        BookResponseDto response = BookResponseDto.builder().id(1L).title("Test Book").build();

        when(modelMapper.map(request, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookResponseDto.class)).thenReturn(response);

        // Act
        BookResponseDto result = bookService.addBook(request);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    void shouldGetBookById_WhenBookExists() {
        Book book = Book.builder().id(1L).title("Java 101").build();
        BookResponseDto response = BookResponseDto.builder().id(1L).title("Java 101").build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookResponseDto.class)).thenReturn(response);

        BookResponseDto result = bookService.getBookById(1L);

        assertEquals("Java 101", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void shouldThrowException_WhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }
}
