package com.getir.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrowing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; // Who borrowed

    @ManyToOne
    private Book book; // Which book

    private LocalDate borrowDate; // The date book was borrowed

    private LocalDate dueDate; // When the book should be returned

    private LocalDate returnDate; // When the book was actually returned
}
