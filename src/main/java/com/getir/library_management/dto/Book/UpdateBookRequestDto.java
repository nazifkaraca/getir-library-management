package com.getir.library_management.dto.Book;

import com.getir.library_management.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// Used when updating book details
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequestDto {

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
    private String isbn;

    private String genre;
    private String publicationDate;
    private boolean availability;
}
