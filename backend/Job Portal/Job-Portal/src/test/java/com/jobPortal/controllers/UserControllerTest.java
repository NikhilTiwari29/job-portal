package com.jobPortal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobPortal.config.SecurityConfig;
import com.jobPortal.dto.LoginDto;
import com.jobPortal.dto.UserDto;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    // Registration Test cases
    @Test
    @DisplayName("Should return 201 when user is registered")
    void testRegisterUser_Success() throws Exception {
        String userJson = """
        {
            "name": "Nikhil",
            "email": "nikhil@example.com",
            "password": "Password@123",
            "accountType": "EMPLOYER"
        }
    """;

        UserDto responseDto = new UserDto(1L, "Nikhil", "nikhil@example.com", null, AccountType.EMPLOYER);

        when(userService.registerUser(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nikhil@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }


    @Test
    @DisplayName("Should return 400 for completely invalid request")
    void testRegisterUser_InvalidRequest() throws Exception {
        String userJson = """
        {
            "name": "Nikhil",
            "email": "",
            "password": "Password@123",
            "accountType": "EMPLOYER"
        }
    """;
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for weak password pattern")
    void testRegisterUser_WeakPassword() throws Exception {
        UserDto userDto = new UserDto(null, "Nikhil", "nikhil@example.com", "weakpass", AccountType.EMPLOYER);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }


    /// Login Test cases

    @Test
    @DisplayName("Should return 200 when user is correctly logged in")
    void testLoginUser_Success() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto("nikhil@example.com", "Password@123");

        UserDto userDto = new UserDto(1L, "Nikhil", "nikhil@example.com", null, AccountType.EMPLOYER);

        when(userService.loginUser(any(LoginDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nikhil@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testLoginUser_MissingEmail_ShouldReturnBadRequest() throws Exception {
        LoginDto loginDto = new LoginDto("", "Password@123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUser_UserNotFound_ShouldReturnNotFound() throws Exception {
        when(userService.loginUser(any(LoginDto.class)))
                .thenThrow(new JobPortalException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        LoginDto loginDto = new LoginDto("unknown@example.com", "Password@123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLoginUser_InvalidPassword_ShouldReturnUnauthorized() throws Exception {
        when(userService.loginUser(any(LoginDto.class)))
                .thenThrow(new JobPortalException("INVALID_CREDENTIALS",HttpStatus.UNAUTHORIZED));

        LoginDto loginDto = new LoginDto("nikhil@example.com", "WrongPassword");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }


}
