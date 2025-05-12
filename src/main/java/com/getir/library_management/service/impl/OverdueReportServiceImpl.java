package com.getir.library_management.service.impl;

import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.service.interfaces.OverdueReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OverdueReportServiceImpl implements OverdueReportService {

    // Repository used to fetch overdue borrowings
    private final BorrowingRepository borrowingRepository;

    // Generates a CSV report for books that are overdue (not returned by their due date)
    public byte[] generateOverdueBooksCsv() {
        // Get today's date to check overdue status
        LocalDate today = LocalDate.now();

        // Fetch borrowings where return date is null and due date is before today
        List<Borrowing> overdueList = borrowingRepository
                .findByReturnDateIsNullAndDueDateBefore(today);

        // Use ByteArrayOutputStream and PrintWriter to generate CSV content
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            // Write CSV header
            writer.println("ID,User,Book,Borrow Date,Due Date");

            // Write each overdue borrowing record as a CSV line
            for (Borrowing b : overdueList) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        b.getId(),
                        b.getUser().getFullName(),
                        b.getBook().getTitle(),
                        b.getBorrowDate(),
                        b.getDueDate()
                );
            }

            // Flush the writer to ensure all content is written
            writer.flush();

            // Return the CSV data as a byte array
            return out.toByteArray();

        } catch (IOException e) {
            // Throw runtime exception if CSV generation fails
            throw new RuntimeException("CSV export failed", e);
        }
    }
}

