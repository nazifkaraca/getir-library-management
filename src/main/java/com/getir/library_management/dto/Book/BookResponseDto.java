package com.getir.library_management.dto.Book;

import com.getir.library_management.entity.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

// Used to return book information to the client
@Data
@NoArgsConstructor
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publicationDate;
    private boolean availability;
}
