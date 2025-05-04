package com.getir.library_management.dto.Book;

import com.getir.library_management.entity.Role;
import lombok.*;

// Used to return book information to the client
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publicationDate;
    private boolean availability;
}
