package com.getir.library_management.controller;

import com.getir.library_management.dto.User.UpdateUserRequestDto;
import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // JWT required for all endpoints
@PreAuthorize("hasRole('LIBRARIAN')")
public class UserController {

    private final UserService userService;

    // Update endpoint - LIBRARIAN only
    // PUT http://localhost:8070/api/user/1
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // Hard delete endpoint (permanently remove the user) - LIBRARIAN only
    // DELETE http://localhost:8070/api/user/hard-delete/1
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        userService.hardDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Soft delete endpoint (mark user as deleted) - LIBRARIAN only
    // DELETE http://localhost:8070/api/user/soft-delete/1
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Get user by id - LIBRARIAN only
    // GET http://localhost:8070/api/user/1
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Get all users - LIBRARIAN only
    // GET http://localhost:8070/api/user/all
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
