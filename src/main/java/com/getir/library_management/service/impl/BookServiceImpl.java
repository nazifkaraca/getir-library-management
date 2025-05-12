package com.getir.library_management.service.impl;

import com.getir.library_management.dto.book.BookResponseDto;
import com.getir.library_management.dto.book.CreateBookRequestDto;
import com.getir.library_management.dto.book.UpdateBookRequestDto;
import com.getir.library_management.entity.Book;
import com.getir.library_management.exception.ExceptionMessages;
import com.getir.library_management.exception.custom.BookAlreadyExistsException;
import com.getir.library_management.exception.custom.BookNotFoundException;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.logging.audit.CurrentUserService;
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

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    // Dependencies required for book operations
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;

    // Adds a new book to the system and evicts cache entries
    @CacheEvict(value = "bookSearchCache", allEntries = true) // Clear redis cache when a new book added
    @Override
    public BookResponseDto addBook(CreateBookRequestDto request) throws BookAlreadyExistsException {
        // Check if book already exists by ISBN
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistsException(ExceptionMessages.BOOK_EXISTS);
        }

        // Map request DTO to Book entity
        Book book = modelMapper.map(request, Book.class);

        // Save the new book to the database
        Book savedBook = bookRepository.save(book);

        // Log the book addition action
        auditLogService.logAction(
                currentUserService.getEmail(),
                "ADD_BOOK",
                "Added book: " + savedBook.getTitle()
        );

        // Map the saved book to a response DTO and return
        return modelMapper.map(savedBook, BookResponseDto.class);
    }

    // Updates an existing book's details
    @Override
    public BookResponseDto updateBook(Long id, UpdateBookRequestDto request) {
        // Retrieve the book or throw an exception if not found
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));

        // Check for duplicate ISBN if it’s being changed
        if (bookRepository.existsByIsbn(request.getIsbn()) && !book.getIsbn().equals(request.getIsbn())) {
            throw new BookAlreadyExistsException(ExceptionMessages.BOOK_EXISTS);
        }

        // Update book fields
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTitle(request.getTitle());
        book.setPublicationDate(request.getPublicationDate());

        // Save updated book
        Book updatedBook = bookRepository.save(book);

        // Log the update action
        auditLogService.logAction(
                currentUserService.getEmail(),
                "UPDATE_BOOK",
                "Updated book ID: " + id
        );

        // Return updated book response
        return modelMapper.map(updatedBook, BookResponseDto.class);
    }

    // Deletes a book by ID
    @Override
    public void deleteBook(Long id) {
        // Retrieve the book or throw an exception if not found
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));

        // Delete the book from the database
        bookRepository.delete(book);

        // Log the deletion
        auditLogService.logAction(
                currentUserService.getEmail(),
                "DELETE_BOOK",
                "Deleted book ID: " + id
        );
    }

    // Retrieves a book by its ID
    @Override
    public BookResponseDto getBookById(Long id) {
        // Retrieve the book or throw an exception if not found
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(ExceptionMessages.BOOK_NOT_FOUND));

        // Map and return the book response DTO
        return modelMapper.map(book, BookResponseDto.class);
    }

    // Searches for books using optional filters and caches the result
    @Cacheable(value = "bookSearchCache", key = "#title + '_' + #author + '_' + #isbn + '_' + #genre + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Override
    public Page<BookResponseDto> searchBooks(String title, String author, String isbn, String genre, Pageable pageable) {
        return bookRepository.searchBooks(title, author, isbn, genre, pageable)
                .map(book -> modelMapper.map(book, BookResponseDto.class));
    }

    // Retrieves all books without any filtering
    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookResponseDto.class))
                .toList();
    }
}
