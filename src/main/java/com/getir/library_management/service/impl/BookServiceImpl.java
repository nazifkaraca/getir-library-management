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
        // Check if book exists by ID
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND));

        // Map update book request with book entity
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTitle(request.getTitle());
        book.setPublicationDate(request.getPublicationDate());

        // Check if book exists by ISBN
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistsException(ErrorMessages.BOOK_EXISTS);
        }

        // Update book
        Book updatedBook = bookRepository.save(book);
        // Return updated book mapped on book response dto
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
                .collect(Collectors.toList());
    }

}
