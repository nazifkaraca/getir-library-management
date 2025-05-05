package com.getir.library_management.controller;

import com.getir.library_management.dto.Book.BookAvailabilityDto;
import com.getir.library_management.service.impl.BookAvailabilityServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

// This controller exposes the book availability stream as a Server-Sent Event (SSE) endpoint
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book/stream")
public class BookStreamController {

    private final BookAvailabilityServiceImpl bookAvailabilityService;

    // Client connects to this endpoint and receives updates as they happen
    @GetMapping(value = "/availability", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookAvailabilityDto> streamAvailability() {
        return bookAvailabilityService.getAvailabilityStream();
    }
}
