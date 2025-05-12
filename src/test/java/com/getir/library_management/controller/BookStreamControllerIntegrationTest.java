package com.getir.library_management.controller;

import com.getir.library_management.dto.Book.BookAvailabilityDto;
import com.getir.library_management.service.impl.BookAvailabilityServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;


@WebFluxTest(BookStreamController.class)
@Import(BookAvailabilityServiceImpl.class)
@ActiveProfiles("test")
class BookStreamControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookAvailabilityServiceImpl bookAvailabilityService;

    @WithMockUser
    @Test
    void testStreamAvailability() {
        BookAvailabilityDto sampleDto = new BookAvailabilityDto((long)1,"Book 1", true);
        Flux<BookAvailabilityDto> mockFlux = Flux.just(sampleDto);

        Mockito.when(bookAvailabilityService.getAvailabilityStream()).thenReturn(mockFlux);

        webTestClient.get()
                .uri("/api/book/stream/availability")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(BookAvailabilityDto.class)
                .hasSize(1)
                .contains(sampleDto);
    }
}
