package com.jobPortal.service;

import com.jobPortal.dto.UserDto;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUser_Success() throws JobPortalException {
        UserDto userDto = new UserDto(null, "Nikhil", "nikhil@example.com", "Password@123", AccountType.EMPLOYER);

        when(userRepository.findByEmail("nikhil@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.registerUser(userDto);

        assertNotNull(result);
        assertThat(result.getEmail()).isEqualTo("nikhil@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception if email already exists")
    void testRegisterUser_EmailExists() {
        UserDto userDto = new UserDto(null, "Nikhil", "nikhil@example.com", "Password@123", AccountType.EMPLOYER);
        when(userRepository.findByEmail("nikhil@example.com")).thenReturn(Optional.of(new User()));

        JobPortalException exception = assertThrows(JobPortalException.class, () -> {
            userService.registerUser(userDto);
        });

        assertThat(exception.getMessage()).isEqualTo("USER_FOUND");
        verify(userRepository, never()).save(any());
    }
}
