package com.getir.library_management.dto.Book;

import lombok.*;

import java.io.Serializable;

// Used to return book information to the client
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDto implements Serializable {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publicationDate;
    private boolean availability;
}
