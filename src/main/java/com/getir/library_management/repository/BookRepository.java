package com.getir.library_management.repository;

import com.getir.library_management.entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>  {
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(@NotBlank(message = "ISBN is required.") String isbn);

    @Query("""
    SELECT b FROM Book b
    WHERE (:title IS NULL OR LOWER(b.title) LIKE CONCAT('%', LOWER(CAST(:title AS string)), '%'))
      AND (:author IS NULL OR LOWER(b.author) LIKE CONCAT('%', LOWER(CAST(:author AS string)), '%'))
      AND (:genre IS NULL OR LOWER(b.genre) LIKE CONCAT('%', LOWER(CAST(:genre AS string)), '%'))
      AND (:isbn IS NULL OR LOWER(b.isbn) LIKE CONCAT('%', LOWER(CAST(:isbn AS string)), '%'))
    """)
    Page<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("isbn") String isbn,
            Pageable pageable
    );
}
