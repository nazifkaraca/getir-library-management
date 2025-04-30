package com.getir.library_management.dto.Book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

// Used when adding a new book to the system
@Data
public class CreateBookRequestDto {

    @Schema(
            description = "Book's title",
            example = "Amat",
            defaultValue = "Untitled"
    )
    @NotBlank(message = "Title is required.")
    private String title;

    @Schema(
            description = "Book's author",
            example = "İhsan Oktay Anar",
            defaultValue = "Anonymous"
    )
    @NotBlank(message = "Author is required.")
    private String author;

    @Schema(
            description = "Book's International Standard Book Number",
            example = "Amat",
            defaultValue = "9789750503726"
    )
    @NotBlank(message = "ISBN is required.")
    private String isbn;

    private String genre;
    private String publicationDate;
    private boolean availability;
}
