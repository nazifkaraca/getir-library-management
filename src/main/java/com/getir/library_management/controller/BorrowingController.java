package com.getir.library_management.controller;

import com.getir.library_management.dto.borrow.BorrowRequestDto;
import com.getir.library_management.dto.borrow.BorrowResponseDto;
import com.getir.library_management.service.interfaces.BorrowingService;
import com.getir.library_management.service.interfaces.OverdueReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final OverdueReportService overdueReportService;

    // Borrow book
    // POST http://localhost:8070/api/borrowing
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BorrowResponseDto> borrowBook(@RequestBody @Valid BorrowRequestDto request) {
        return new ResponseEntity<>(borrowingService.borrowBook(request), HttpStatus.CREATED);
    }

    // Get all borrowings of a user
    // GET http://localhost:8070/api/borrowing/user/1
    @GetMapping("/user/{id}")
    public ResponseEntity<List<BorrowResponseDto>>  getBorrowingsOfUser(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.getBorrowingsByUser(id));
    }

    // Get all borrowings - LIBRARIAN only
    // GET http://localhost:8070/api/borrowing/all
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/all")
    public ResponseEntity<List<BorrowResponseDto>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }

    // Get overdue books - LIBRARIAN only
    // GET http://localhost:8070/api/borrowing/overdue
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowResponseDto>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBooks());
    }

    // Get all borrowings report - LIBRARIAN only
    // GET http://localhost:8070/api/borrowing/overdue/export
    @GetMapping("/overdue/export")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<byte[]> exportOverdueBooksCsv() {
        byte[] csv = overdueReportService.generateOverdueBooksCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue_books.csv");

        return ResponseEntity.ok().headers(headers).body(csv);
    }

    // Return borrowed book
    // PUT http://localhost:8070/api/borrowing/1
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/return/{id}")
    public ResponseEntity<BorrowResponseDto>  returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }
}
