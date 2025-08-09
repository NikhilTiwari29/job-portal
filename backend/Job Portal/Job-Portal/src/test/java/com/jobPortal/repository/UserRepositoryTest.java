package com.jobPortal.repository;

import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User(null, "Nikhil", "nikhil@example.com", "encodedPassword123", AccountType.APPLICANT);
    }

    @Test
    @DisplayName("Register: Should save user successfully")
    void testRegisterUser_Success() {
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        assertNotNull(savedUser.getId());
        assertThat(savedUser.getName()).isEqualTo("Nikhil");
        assertThat(savedUser.getEmail()).isEqualTo("nikhil@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
        assertThat(savedUser.getAccountType()).isEqualTo(AccountType.APPLICANT);
    }

    @Test
    @DisplayName("Login: Should return user when email exists")
    void testLoginUser_WhenEmailExists() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> result = userRepository.findByEmail("nikhil@example.com");

        // Assert
        assertThat(result).isPresent();
        User foundUser = result.get();
        assertThat(foundUser.getEmail()).isEqualTo("nikhil@example.com");
        assertThat(foundUser.getPassword()).isEqualTo("encodedPassword123"); // simulate password check in service
    }

    @Test
    @DisplayName("Login: Should return empty when email does not exist")
    void testLoginUser_WhenEmailDoesNotExist() {
        // Act
        Optional<User> result = userRepository.findByEmail("random@example.com");

        // Assert
        assertThat(result).isEmpty();
    }
}
