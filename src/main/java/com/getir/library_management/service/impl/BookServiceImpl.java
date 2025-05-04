package com.getir.library_management.service.impl;

import com.getir.library_management.dto.Book.BookResponseDto;
import com.getir.library_management.dto.Book.CreateBookRequestDto;
import com.getir.library_management.dto.Book.UpdateBookRequestDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.exception.ErrorMessages;
import com.getir.library_management.exception.custom.BookAlreadyExistsException;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.service.interfaces.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @CacheEvict(value = "bookSearchCache", allEntries = true) // Clear redis cache when a new book added
    @Override
    public BookResponseDto addBook(CreateBookRequestDto request) throws BookAlreadyExistsException {
        // Check if book exists
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistsException(ErrorMessages.BOOK_EXISTS);
        }
        // Map requested book to be created with Book entity
        Book book = modelMapper.map(request, Book.class);
        // Save book
        Book savedBook = bookRepository.save(book);
        // Return mapped book response
        return modelMapper.map(savedBook, BookResponseDto.class);
    }

    @Override
    public BookResponseDto updateBook(Long id, UpdateBookRequestDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));

        // Check before setting ISBN
        if (bookRepository.existsByIsbn(request.getIsbn()) && !book.getIsbn().equals(request.getIsbn())) {
            throw new BookAlreadyExistsException(ErrorMessages.BOOK_EXISTS);
        }

        // Set after validation
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTitle(request.getTitle());
        book.setPublicationDate(request.getPublicationDate());

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookResponseDto.class);
    }

    @Override
    public void deleteBook(Long id) {
        // Find book by id
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));

        // Delete book
        bookRepository.delete(book);
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        // Find book by id
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));
        // Return book by mapping
        return modelMapper.map(book, BookResponseDto.class);
    }

    @Cacheable(value = "bookSearchCache", key = "#title + '_' + #author + '_' + #isbn + '_' + #genre + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Override
    public Page<BookResponseDto> searchBooks(String title, String author, String isbn, String genre, Pageable pageable) {
        return bookRepository.searchBooks(title, author, isbn, genre, pageable)
                .map(book -> modelMapper.map(book, BookResponseDto.class));
    }

    // Get all books without filter
    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookResponseDto.class))
                .toList();
    }

}
