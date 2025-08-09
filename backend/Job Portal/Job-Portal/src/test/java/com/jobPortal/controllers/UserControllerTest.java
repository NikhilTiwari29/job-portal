package com.jobPortal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobPortal.config.SecurityConfig;
import com.jobPortal.dto.*;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------
    // ✅ Registration Test Cases
    // -----------------------------

    @Test
    @DisplayName("✅ Should return 201 when user is successfully registered")
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

        // Checks if user is created and sensitive info (like password) is not leaked
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nikhil@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("❌ Should return 400 when required fields are missing or empty")
    void testRegisterUser_InvalidRequest() throws Exception {
        String userJson = """
        {
            "name": "Nikhil",
            "email": "",
            "password": "Password@123",
            "accountType": "EMPLOYER"
        }
        """;

        // Should fail due to blank email field
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Should return 400 when password is weak")
    void testRegisterUser_WeakPassword() throws Exception {
        UserDto userDto = new UserDto(null, "Nikhil", "nikhil@example.com", "weakpass", AccountType.EMPLOYER);

        // Validation should fail for weak password
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------
    // ✅ Login Test Cases
    // -----------------------------

    @Test
    @DisplayName("✅ Should return 200 for valid login credentials")
    void testLoginUser_Success() throws Exception {
        LoginDto loginDto = new LoginDto("nikhil@example.com", "Password@123");
        UserDto userDto = new UserDto(1L, "Nikhil", "nikhil@example.com", null, AccountType.EMPLOYER);

        when(userService.loginUser(any(LoginDto.class))).thenReturn(userDto);

        // Valid credentials should login successfully
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nikhil@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("❌ Should return 400 when email is missing in login")
    void testLoginUser_MissingEmail_ShouldReturnBadRequest() throws Exception {
        LoginDto loginDto = new LoginDto("", "Password@123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Should return 404 when user does not exist")
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
    @DisplayName("❌ Should return 401 for incorrect password")
    void testLoginUser_InvalidPassword_ShouldReturnUnauthorized() throws Exception {
        when(userService.loginUser(any(LoginDto.class)))
                .thenThrow(new JobPortalException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED));

        LoginDto loginDto = new LoginDto("nikhil@example.com", "WrongPassword");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    // -----------------------------
    // ✅ Send OTP Test Cases
    // -----------------------------

    @Test
    @DisplayName("✅ Should send OTP successfully and return 200")
    void shouldSendOtpSuccessfully() throws Exception {
        OtpEmailRequest otpEmailRequest = new OtpEmailRequest("test@example.com");

        // Should return 200 OK and invoke service method
        mockMvc.perform(post("/users/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));

        verify(userService, times(1)).sendOtp(otpEmailRequest.getEmail());
    }

    @Test
    @DisplayName("❌ Should return 400 for invalid email format")
    void shouldFailOnInvalidEmailFormat() throws Exception {
        OtpEmailRequest otpEmailRequest = new OtpEmailRequest("testexample"); // invalid email

        mockMvc.perform(post("/users/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Should return 400 for missing email in OTP request")
    void shouldFailOnMissingEmail() throws Exception {
        OtpEmailRequest otpEmailRequest = new OtpEmailRequest(); // blank email

        mockMvc.perform(post("/users/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Should return 500 if service layer throws exception during OTP")
    void shouldReturnErrorIfServiceFails() throws Exception {
        OtpEmailRequest otpEmailRequest = new OtpEmailRequest("test@example.com");

        doThrow(new RuntimeException("SMTP server down"))
                .when(userService).sendOtp(otpEmailRequest.getEmail());

        mockMvc.perform(post("/users/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpEmailRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp_Success() throws Exception {
        // Arrange
        OtpVerificationRequest request = new OtpVerificationRequest("user@example.com", "123456");

        // Service method doesn't return anything
        doNothing().when(userService).verifyOtp(request.getEmail(), request.getOtp());

        // Act & Assert
        mockMvc.perform(post("/users/verify/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP verified successfully."));

        verify(userService, times(1)).verifyOtp(request.getEmail(), request.getOtp());
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() throws Exception {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("user@example.com","newPass456");

        doNothing().when(userService).changePassword(request);

        // Act & Assert
        mockMvc.perform(post("/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userService, times(1)).changePassword(request);
    }

    @Test
    @DisplayName("Should return 400 when OTP request is invalid")
    void testVerifyOtp_InvalidRequest() throws Exception {
        OtpVerificationRequest request = new OtpVerificationRequest("", ""); // Invalid

        mockMvc.perform(post("/users/verify/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).verifyOtp(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 400 when change password request is invalid")
    void testChangePassword_InvalidRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("", "");

        mockMvc.perform(post("/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(any());
    }
}
