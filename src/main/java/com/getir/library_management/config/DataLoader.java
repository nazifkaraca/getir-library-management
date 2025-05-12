package com.getir.library_management.config;

import com.getir.library_management.entity.Book;
import com.getir.library_management.entity.Borrowing;
import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import com.getir.library_management.repository.BookRepository;
import com.getir.library_management.repository.BorrowingRepository;
import com.getir.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile({"!prod", "!test"})
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            List<User> users = List.of(
                    User.builder().fullName("Libby Librarian").email("librarian@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_LIBRARIAN).build(),
                    User.builder().fullName("John Reader").email("john@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Alice Smith").email("alice@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Bob Johnson").email("bob@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Carol White").email("carol@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("David Brown").email("david@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Eve Green").email("eve@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Frank Gray").email("frank@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build(),
                    User.builder().fullName("Grace Black").email("grace@getir.com").password(passwordEncoder.encode("1234")).role(Role.ROLE_USER).build()
            );
            userRepository.saveAll(users);
        }

        if (bookRepository.count() == 0) {
            List<Book> books = List.of(
                    newBook("Clean Code", "Robert C. Martin", "9780132350884", "Programming", "2008"),
                    newBook("Effective Java", "Joshua Bloch", "9780134685991", "Programming", "2018"),
                    newBook("The Pragmatic Programmer", "Andy Hunt", "9780201616224", "Programming", "1999"),
                    newBook("Design Patterns", "Erich Gamma", "9780201633610", "Software Engineering", "1994"),
                    newBook("Refactoring", "Martin Fowler", "9780201485677", "Programming", "1999"),
                    newBook("Head First Design Patterns", "Eric Freeman", "9780596007126", "Programming", "2004"),
                    newBook("Domain-Driven Design", "Eric Evans", "9780321125217", "Software Architecture", "2003"),
                    newBook("Java Concurrency in Practice", "Brian Goetz", "9780321349606", "Programming", "2006"),
                    newBook("Spring in Action", "Craig Walls", "9781617294945", "Java", "2018"),
                    newBook("You Don't Know JS", "Kyle Simpson", "9781491904244", "JavaScript", "2015"),
                    newBook("Cracking the Coding Interview", "Gayle McDowell", "9780984782857", "Interview Prep", "2015"),
                    newBook("Introduction to Algorithms", "Thomas H. Cormen", "9780262033848", "Algorithms", "2009"),
                    newBook("The Clean Coder", "Robert C. Martin", "9780137081073", "Craftsmanship", "2011"),
                    newBook("SICP", "Harold Abelson", "9780262510875", "Computer Science", "1996"),
                    newBook("Code Complete", "Steve McConnell", "9780735619678", "Programming", "2004"),
                    newBook("TAOCP", "Donald Knuth", "9780201896831", "Algorithms", "1997"),
                    newBook("Algorithms", "Robert Sedgewick", "9780321573513", "Computer Science", "2011"),
                    newBook("Python Crash Course", "Eric Matthes", "9781593279288", "Python", "2019"),
                    newBook("Deep Learning", "Ian Goodfellow", "9780262035613", "AI", "2016"),
                    newBook("AI: A Modern Approach", "Stuart Russell", "9780136042594", "AI", "2010"),
                    newBook("The Mythical Man-Month", "Fred Brooks", "9780201835953", "Project Management", "1995")
            );
            bookRepository.saveAll(books);
        }

        if (borrowingRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Book> books = bookRepository.findAll();

            if (!users.isEmpty() && !books.isEmpty()) {
                List<Borrowing> borrowings = List.of(
                        // 🟢 On-time borrowings
                        newBorrow(users.get(0), books.get(0), 7, -7, null),
                        newBorrow(users.get(1), books.get(1), 10, -2, LocalDate.now().minusDays(1)),
                        newBorrow(users.get(2), books.get(2), 2, -3, LocalDate.now().plusDays(4)),

                        // 🔴 Overdue not returned
                        newBorrow(users.get(3), books.get(3), 20, 5, null),
                        newBorrow(users.get(5), books.get(5), 30, 2, null),

                        // 🔴 Overdue returned late
                        newBorrow(users.get(4), books.get(4), 15, 7, LocalDate.now().minusDays(1))
                );
                borrowingRepository.saveAll(borrowings);
            }
        }
    }

    private Book newBook(String title, String author, String isbn, String genre, String year) {
        return Book.builder().title(title).author(author).isbn(isbn).genre(genre).publicationDate(year).availability(true).build();
    }

    private Borrowing newBorrow(User user, Book book, int borrowAgo, int dueAgo, LocalDate returnDate) {
        book.setAvailability(false);
        bookRepository.save(book);

        return Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now().minusDays(borrowAgo))
                .dueDate(LocalDate.now().minusDays(dueAgo))
                .returnDate(returnDate)
                .build();
    }
}
