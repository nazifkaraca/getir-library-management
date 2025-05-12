package com.getir.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.library_management.dto.book.CreateBookRequestDto;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.logging.audit.AuditLogService;
import com.getir.library_management.logging.audit.CurrentUserService;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.impl.BookAvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean
    private BookAvailabilityServiceImpl bookAvailabilityService;


    private String token;

    @BeforeEach
    void setup() throws Exception {

        borrowingRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        // Kullanıcıyı kayıt et
        User user = User.builder()
                .fullName("Test User")
                .email("test@getir.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.ROLE_LIBRARIAN)
                .build();
        userRepository.save(user);

        // Login olup token al
        String loginPayload = """
                {
                  "email": "test@getir.com",
                  "password": "1234"
                }
                """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        this.token = "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldAddBookSuccessfully() throws Exception {
        CreateBookRequestDto request = CreateBookRequestDto.builder()
                .title("Test Book")
                .author("Author A")
                .isbn("1234567890123")
                .genre("Drama")
                .availability(true)
                .build();

        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    @Test
    void shouldFailToAddBookWithDuplicateIsbn() throws Exception {
        String duplicateIsbn = "1111111111111";

        CreateBookRequestDto request = CreateBookRequestDto.builder()
                .title("Book A")
                .author("Author X")
                .isbn(duplicateIsbn)
                .genre("Sci-Fi")
                .availability(true)
                .build();

        // İlk ekleme başarılı
        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // İkinci ekleme başarısız
        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A book with the same ISBN already exists in the library. Duplicate entries are not allowed."));
    }

    @Test
    void shouldSearchBooksWithTitleFilter() throws Exception {
        CreateBookRequestDto request = CreateBookRequestDto.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9999999999999")
                .genre("Software")
                .availability(true)
                .build();

        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/book/search")
                        .header("Authorization", token)
                        .param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));
    }

    @Test
    void shouldListAllBooks() throws Exception {
        CreateBookRequestDto request1 = CreateBookRequestDto.builder()
                .title("Book 1")
                .author("Author 1")
                .isbn("1010101010101")
                .genre("Horror")
                .availability(true)
                .build();

        CreateBookRequestDto request2 = CreateBookRequestDto.builder()
                .title("Book 2")
                .author("Author 2")
                .isbn("2020202020202")
                .genre("Comedy")
                .availability(true)
                .build();

        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/book/all")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentBook() throws Exception {
        mockMvc.perform(delete("/api/book/{id}", 9999L)
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The requested book could not be found in the system. Please check the book ID or try a different search term."));
    }

    @Test
    void shouldDeleteBookSuccessfully() throws Exception {
        CreateBookRequestDto request = CreateBookRequestDto.builder()
                .title("To Be Deleted")
                .author("Anonymous")
                .isbn("3333333333333")
                .genre("History")
                .availability(true)
                .build();

        String content = mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookId = objectMapper.readTree(content).get("id").asLong();

        mockMvc.perform(delete("/api/book/{id}", bookId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateBookSuccessfully() throws Exception {
        CreateBookRequestDto request = CreateBookRequestDto.builder()
                .title("Original Title")
                .author("Author")
                .isbn("4444444444444")
                .genre("Mystery")
                .availability(true)
                .build();

        String response = mockMvc.perform(post("/api/book")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookId = objectMapper.readTree(response).get("id").asLong();

        String updatePayload = """
            {
                "id": %d,
                "title": "Updated Title",
                "author": "Author",
                "isbn": "4444444444444",
                "genre": "Mystery",
                "availability": true
            }
            """.formatted(bookId);

        mockMvc.perform(put("/api/book/" + bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }
}
