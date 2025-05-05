package com.getir.library_management.controller;

import com.getir.library_management.dto.Book.BookResponseDto;
import com.getir.library_management.dto.Book.CreateBookRequestDto;
import com.getir.library_management.dto.Book.UpdateBookRequestDto;
import com.getir.library_management.service.impl.BookServiceImpl;
import com.getir.library_management.service.interfaces.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.aop.support.AbstractExpressionPointcut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // JWT required for all endpoints
public class BookController {

    private final BookService bookService;

    // Add book - LIBRARIAN only
    // POST http://localhost:8070/api/book
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@Valid @RequestBody CreateBookRequestDto request) {
        return new ResponseEntity<>(bookService.addBook(request), HttpStatus.CREATED);
    }

    // Get book by ID - public
    // GET http://localhost:8070/api/book/1
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // Get all books - public
    @GetMapping("/all")
    // GET http://localhost:8070/api/book/all
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // Search books with pagination - public
    // GET http://localhost:8070/api/book/search?title=java&page=0&size=10&sort=title,asc
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDto>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String genre,
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(title, author, isbn, genre, pageable));
    }

    // Update book - LIBRARIAN only
    // PUT http://localhost:8070/api/book/1
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateBookRequestDto request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    // Delete book - LIBRARIAN only
    // DELETE http://localhost:8070/api/book/1
    @PreAuthorize("hasRole('LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
