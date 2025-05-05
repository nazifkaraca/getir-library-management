package com.getir.library_management.dto.Book;

// A simple DTO to represent real-time availability information
public record BookAvailabilityDto(Long bookId, String title, boolean available) {}

