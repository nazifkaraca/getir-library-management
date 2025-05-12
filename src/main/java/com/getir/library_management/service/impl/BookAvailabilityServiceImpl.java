package com.getir.library_management.service.impl;

import com.getir.library_management.dto.book.BookAvailabilityDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

// This service manages a reactive stream of book availability updates
@Service
public class BookAvailabilityServiceImpl {

    // Sink is used to emit new events to subscribers
    private final Sinks.Many<BookAvailabilityDto> sink = Sinks.many().multicast().onBackpressureBuffer();

    // Called to emit an availability update to all active subscribers
    public void publishAvailabilityUpdate(BookAvailabilityDto update) {
        sink.tryEmitNext(update);
    }

    // Exposes the availability stream for controllers to return as a Flux
    public Flux<BookAvailabilityDto> getAvailabilityStream() {
        return sink.asFlux();
    }
}
