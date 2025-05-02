package com.getir.library_management.config;

import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!prod") // Do not run in production
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Add sample users
            User librarian = User.builder()
                    .fullName("Libby Librarian")
                    .email("librarian@getir.com")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ROLE_LIBRARIAN)
                    .build();

            User regularUser = User.builder()
                    .fullName("John Reader")
                    .email("user@getir.com")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ROLE_USER)
                    .build();

            userRepository.save(librarian);
            userRepository.save(regularUser);
        }

        if (bookRepository.count() == 0) {
            // Add sample books
            bookRepository.save(Book.builder().title("Clean Code").author("Robert C. Martin").isbn("9780132350884").genre("Programming").publicationDate("2008").availability(true).build());
            bookRepository.save(Book.builder().title("Effective Java").author("Joshua Bloch").isbn("9780134685991").genre("Programming").publicationDate("2018").availability(true).build());
            bookRepository.save(Book.builder().title("The Pragmatic Programmer").author("Andy Hunt").isbn("9780201616224").genre("Programming").publicationDate("1999").availability(true).build());
            bookRepository.save(Book.builder().title("Design Patterns").author("Erich Gamma").isbn("9780201633610").genre("Software Engineering").publicationDate("1994").availability(true).build());
            bookRepository.save(Book.builder().title("Refactoring").author("Martin Fowler").isbn("9780201485677").genre("Programming").publicationDate("1999").availability(true).build());
            bookRepository.save(Book.builder().title("Head First Design Patterns").author("Eric Freeman").isbn("9780596007126").genre("Programming").publicationDate("2004").availability(true).build());
            bookRepository.save(Book.builder().title("Domain-Driven Design").author("Eric Evans").isbn("9780321125217").genre("Software Architecture").publicationDate("2003").availability(true).build());
            bookRepository.save(Book.builder().title("Java Concurrency in Practice").author("Brian Goetz").isbn("9780321349606").genre("Programming").publicationDate("2006").availability(true).build());
            bookRepository.save(Book.builder().title("Spring in Action").author("Craig Walls").isbn("9781617294945").genre("Java").publicationDate("2018").availability(true).build());
            bookRepository.save(Book.builder().title("You Don't Know JS").author("Kyle Simpson").isbn("9781491904244").genre("JavaScript").publicationDate("2015").availability(true).build());
            bookRepository.save(Book.builder().title("Cracking the Coding Interview").author("Gayle Laakmann McDowell").isbn("9780984782857").genre("Interview Prep").publicationDate("2015").availability(true).build());
            bookRepository.save(Book.builder().title("Introduction to Algorithms").author("Thomas H. Cormen").isbn("9780262033848").genre("Algorithms").publicationDate("2009").availability(true).build());
            bookRepository.save(Book.builder().title("The Clean Coder").author("Robert C. Martin").isbn("9780137081073").genre("Software Craftsmanship").publicationDate("2011").availability(true).build());
            bookRepository.save(Book.builder().title("Structure and Interpretation of Computer Programs").author("Harold Abelson").isbn("9780262510875").genre("Computer Science").publicationDate("1996").availability(true).build());
            bookRepository.save(Book.builder().title("Code Complete").author("Steve McConnell").isbn("9780735619678").genre("Programming").publicationDate("2004").availability(true).build());
            bookRepository.save(Book.builder().title("The Art of Computer Programming").author("Donald E. Knuth").isbn("9780201896831").genre("Algorithms").publicationDate("1997").availability(true).build());
            bookRepository.save(Book.builder().title("Algorithms").author("Robert Sedgewick").isbn("9780321573513").genre("Computer Science").publicationDate("2011").availability(true).build());
            bookRepository.save(Book.builder().title("Python Crash Course").author("Eric Matthes").isbn("9781593279288").genre("Python").publicationDate("2019").availability(true).build());
            bookRepository.save(Book.builder().title("Deep Learning").author("Ian Goodfellow").isbn("9780262035613").genre("AI").publicationDate("2016").availability(true).build());
            bookRepository.save(Book.builder().title("Artificial Intelligence: A Modern Approach").author("Stuart Russell").isbn("9780136042594").genre("AI").publicationDate("2010").availability(true).build());
            bookRepository.save(Book.builder().title("The Mythical Man-Month").author("Frederick P. Brooks Jr.").isbn("9780201835953").genre("Software Project Management").publicationDate("1995").availability(true).build());
        }
    }
}
