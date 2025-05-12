package com.getir.library_management.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// Used when adding a new book to the system
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull(message = "Availability is required.")
    private boolean availability;
}
