package com.jobPortal.repository;

import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init(){
        // ARRANGE
        user = new User(null,"Nikhil", "nikhil@example.com", "password123", AccountType.EMPLOYER);
    }

    @Test
    @DisplayName("Should save user")
    void testUserSave(){
        // ACT
        User savedUser = userRepository.save(user);

        // ASSERT
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        Assertions.assertEquals("Nikhil", savedUser.getName());
        Assertions.assertEquals("nikhil@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("Should return user when email exists")
    void testFindByEmail_WhenEmailExists() {
        User user = new User(null, "Nikhil", "nikhil@example.com", "password123", AccountType.EMPLOYER);
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("nikhil@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("nikhil@example.com");
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void testFindByEmail_WhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("random@example.com");

        assertThat(result).isEmpty();
    }
}
