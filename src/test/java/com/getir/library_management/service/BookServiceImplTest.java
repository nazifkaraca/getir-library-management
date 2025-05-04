
package com.getir.library_management.service;

import com.getir.library_management.dto.Book.BookResponseDto;
import com.getir.library_management.dto.Book.CreateBookRequestDto;
import com.getir.library_management.dto.Book.UpdateBookRequestDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.exception.custom.BookAlreadyExistsException;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBook_ShouldAddSuccessfully() {
        CreateBookRequestDto request = new CreateBookRequestDto("Title", "Author", "123456", "Genre", "2022", true);
        Book book = Book.builder().title("Title").author("Author").isbn("123456").build();
        Book savedBook = Book.builder().id(1L).title("Title").author("Author").isbn("123456").build();
        BookResponseDto responseDto = new BookResponseDto(1L, "Title", "Author", "123456", "Genre", "2022", true);

        when(bookRepository.existsByIsbn("123456")).thenReturn(false);
        when(modelMapper.map(request, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookResponseDto.class)).thenReturn(responseDto);

        BookResponseDto result = bookService.addBook(request);
        assertEquals("123456", result.getIsbn());
    }

    @Test
    void addBook_ShouldThrow_WhenIsbnExists() {
        CreateBookRequestDto request = new CreateBookRequestDto("Title", "Author", "123456", "Genre", "2022", true);
        when(bookRepository.existsByIsbn("123456")).thenReturn(true);
        assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(request));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() {
        Book book = Book.builder().id(1L).isbn("123456").build();
        BookResponseDto dto = new BookResponseDto(1L, "Title", "Author", "123456", "Genre", "2022", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookResponseDto.class)).thenReturn(dto);
        BookResponseDto result = bookService.getBookById(1L);
        assertEquals("123456", result.getIsbn());
    }

    @Test
    void getBookById_ShouldThrow_WhenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void deleteBook_ShouldDelete_WhenExists() {
        Book book = Book.builder().id(1L).isbn("123456").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.deleteBook(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void updateBook_ShouldUpdate_WhenExists() {
        Long bookId = 1L;
        UpdateBookRequestDto request = new UpdateBookRequestDto("New Title", "New Author", "999", "Genre", "2023", true);
        Book existing = Book.builder().id(bookId).isbn("old").build();
        Book updated = Book.builder().id(bookId).isbn("999").build();
        BookResponseDto dto = new BookResponseDto(bookId, "New Title", "New Author", "999", "Genre", "2023", true);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbn("999")).thenReturn(false);
        when(bookRepository.save(existing)).thenReturn(updated);
        when(modelMapper.map(updated, BookResponseDto.class)).thenReturn(dto);

        BookResponseDto result = bookService.updateBook(bookId, request);
        assertEquals("999", result.getIsbn());
    }

    @Test
    void updateBook_ShouldThrow_WhenIsbnExists() {
        UpdateBookRequestDto request = new UpdateBookRequestDto("Title", "Author", "123456", "Genre", "2022", true);
        Book book = Book.builder().id(1L).isbn("old").build(); // farklı ISBN

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbn("123456")).thenReturn(true); // çakışma var

        assertThrows(BookAlreadyExistsException.class, () -> bookService.updateBook(1L, request));
    }

    @Test
    void getAllBooks_ShouldReturnList() {
        Book b1 = Book.builder().id(1L).isbn("a").build();
        Book b2 = Book.builder().id(2L).isbn("b").build();
        BookResponseDto d1 = new BookResponseDto(1L, "t1", "a1", "a", "", "", true);
        BookResponseDto d2 = new BookResponseDto(2L, "t2", "a2", "b", "", "", true);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(b1, b2));
        when(modelMapper.map(b1, BookResponseDto.class)).thenReturn(d1);
        when(modelMapper.map(b2, BookResponseDto.class)).thenReturn(d2);

        List<BookResponseDto> list = bookService.getAllBooks();
        assertEquals(2, list.size());
    }

    @Test
    void searchBooks_ShouldReturnPagedResult() {
        Book book = Book.builder().id(1L).isbn("123").build();
        BookResponseDto dto = new BookResponseDto(1L, "t", "a", "123", "", "", true);
        Page<Book> page = new PageImpl<>(List.of(book));
        PageRequest pageable = PageRequest.of(0, 10);

        when(bookRepository.searchBooks(null, null, null, null, pageable)).thenReturn(page);
        when(modelMapper.map(book, BookResponseDto.class)).thenReturn(dto);

        Page<BookResponseDto> result = bookService.searchBooks(null, null, null, null, pageable);
        assertEquals(1, result.getTotalElements());
    }
}
