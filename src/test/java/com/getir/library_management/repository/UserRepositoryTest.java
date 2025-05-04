package com.getir.library_management.repository;

import com.getir.library_management.entity.Role;
import com.getir.library_management.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .fullName("John Doe")
                .email("john.doe@getir.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .markedAsDeleted(false)
                .build();

        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("john.doe@getir.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("john.doe@getir.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("notfound@getir.com");
        assertFalse(foundUser.isPresent());
    }
}
