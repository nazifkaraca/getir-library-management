package com.getir.library_management.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Error response model returned when an API request fails.")
public class ErrorResponse {

    @Schema(description = "Time when the error occurred", example = "2025-05-06T03:15:30.123")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Short description of the error", example = "Book Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "The book with ID 1234 was not found.")
    private String message;
}
