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
    private final BorrowingRepository borrowingRepository;

    public byte[] generateOverdueBooksCsv() {
        LocalDate today = LocalDate.now();

        List<Borrowing> overdueList = borrowingRepository
                .findByReturnDateIsNullAndDueDateBefore(today);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            writer.println("ID,User,Book,Borrow Date,Due Date");

            for (Borrowing b : overdueList) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        b.getId(),
                        b.getUser().getFullName(),
                        b.getBook().getTitle(),
                        b.getBorrowDate(),
                        b.getDueDate()
                );
            }

            writer.flush();
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("CSV export failed", e);
        }
    }
}
