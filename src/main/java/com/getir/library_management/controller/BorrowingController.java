package com.getir.library_management.controller;

import com.getir.library_management.dto.Borrow.BorrowRequestDto;
import com.getir.library_management.dto.Borrow.BorrowResponseDto;
import com.getir.library_management.service.interfaces.BorrowingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowing")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Jwt required for all endpoints
public class BorrowingController {

    private final BorrowingService borrowingService;

    // Borrow book
    // POST http://localhost:8070/api/borrowing
    @PostMapping
    public ResponseEntity<BorrowResponseDto> borrowBook(@RequestBody @Valid BorrowRequestDto request) {
        return new ResponseEntity<>(borrowingService.borrowBook(request), HttpStatus.CREATED);
    }

    // Return borrowed book
    // PUT http://localhost:8070/api/borrowing/1
    @PutMapping("/return/{id}")
    public ResponseEntity<BorrowResponseDto>  returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    // Get all borrowings of a user
    // GET http://localhost:8070/api/borrowing/user/1
    @GetMapping("/user/{id}")
    public ResponseEntity<List<BorrowResponseDto>>  getBorrowingsOfUser(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.getBorrowingsByUser(id));
    }

    // Get overdue books - LIBRARIAN only
    // GET http://localhost:8070/api/borrowing/overdue
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowResponseDto>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBooks());
    }

    // Get all borrowings - LIBRARIAN only
    // GET http://localhost:8070/api/borrowing/all
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/all")
    public ResponseEntity<List<BorrowResponseDto>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }
}
