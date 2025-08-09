package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequest;
import com.jobPortal.dto.LoginDto;
import com.jobPortal.dto.UserDto;
import com.jobPortal.entity.Otp;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.OtpRepository;
import com.jobPortal.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

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

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginUser_Success() throws JobPortalException {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setAccountType(AccountType.APPLICANT);

        when(userRepository.findByEmail(loginDto.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginDto.getPassword(), mockUser.getPassword()))
                .thenReturn(true);

        // Act
        UserDto result = userService.loginUser(loginDto);

        // Assert
        assertNotNull(result);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test");
        assertThat(result.getAccountType()).isEqualTo(AccountType.APPLICANT);

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordEncoder).matches(loginDto.getPassword(), mockUser.getPassword());
    }


    @Test
    @DisplayName("Should throw USER_NOT_FOUND if email doesn't exist")
    void testLoginUser_UserNotFound() {
        LoginDto loginDto = new LoginDto("notfound@example.com", "password123");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        JobPortalException exception = assertThrows(JobPortalException.class, () -> {
            userService.loginUser(loginDto);
        });

        assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");
        verify(userRepository).findByEmail(loginDto.getEmail());
    }

    @Test
    @DisplayName("Should throw INVALID_CREDENTIALS for wrong password")
    void testLoginUser_InvalidPassword() {
        LoginDto loginDto = new LoginDto("test@example.com", "wrongPassword");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginDto.getPassword(), mockUser.getPassword())).thenReturn(false);

        JobPortalException exception = assertThrows(JobPortalException.class, () -> {
            userService.loginUser(loginDto);
        });

        assertThat(exception.getMessage()).isEqualTo("INVALID_CREDENTIALS");

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordEncoder).matches(loginDto.getPassword(), mockUser.getPassword());
    }

    @Test
    @DisplayName("Should send OTP successfully")
    void testSendOtp_Success() throws Exception {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(otpRepository.save(any(Otp.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        MimeMessageHelper helper = mock(MimeMessageHelper.class);

        Boolean result = userService.sendOtp(email);

        assertThat(result).isTrue();

        verify(userRepository).findByEmail(email);
        verify(otpRepository).save(any(Otp.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should throw USER_NOT_FOUND if email is not registered")
    void testSendOtp_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        JobPortalException exception = assertThrows(JobPortalException.class, () -> {
            userService.sendOtp(email);
        });

        assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");
        verify(userRepository).findByEmail(email);
        verifyNoInteractions(mailSender);
    }

    @Test
    @DisplayName("Should throw FAILED_TO_SEND_OTP if mail sending fails")
    void testSendOtp_MailSendFailure() throws Exception {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(otpRepository.save(any(Otp.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

        JobPortalException exception = assertThrows(JobPortalException.class, () -> {
            userService.sendOtp(email);
        });

        assertThat(exception.getMessage()).isEqualTo("FAILED_TO_SEND_OTP");
        verify(userRepository).findByEmail(email);
        verify(otpRepository).save(any(Otp.class));
        verify(mailSender).createMimeMessage();
    }

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        otp = new Otp();
//        otp.setEmail("user@example.com");
//        otp.setOtp("123456");
//        otp.setCreationTime(LocalDateTime.now());
//        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
//
//        user = new User(1L, "John Doe", "user@example.com", "encodedOldPass", AccountType.EMPLOYEE);
//    }

    // ---------- verifyOtp ----------

    @Test
    @DisplayName("verifyOtp - success")
    void verifyOtp_Success() throws Exception {
        Otp otp = new Otp();
        otp.setEmail("user@example.com");
        otp.setOtp("123456");
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(otpRepository.findTopByEmailOrderByCreationTimeDesc("user@example.com"))
                .thenReturn(Optional.of(otp));

        userService.verifyOtp("user@example.com", "123456");

        verify(otpRepository, times(1)).delete(otp);
    }

    @Test
    @DisplayName("verifyOtp - OTP not found")
    void verifyOtp_OtpNotFound() {
        Otp otp = new Otp();
        otp.setEmail("user@example.com");
        otp.setOtp("123456");
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(otpRepository.findTopByEmailOrderByCreationTimeDesc("user@example.com"))
                .thenReturn(Optional.empty());

        JobPortalException ex = assertThrows(JobPortalException.class,
                () -> userService.verifyOtp("user@example.com", "123456"));

        assertEquals("OTP_NOT_FOUND", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("verifyOtp - invalid OTP")
    void verifyOtp_InvalidOtp() {
        Otp otp = new Otp();
        otp.setEmail("user@example.com");
        otp.setOtp("222222");
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(otpRepository.findTopByEmailOrderByCreationTimeDesc("user@example.com"))
                .thenReturn(Optional.of(otp));

        JobPortalException ex = assertThrows(JobPortalException.class,
                () -> userService.verifyOtp("user@example.com", "123456"));

        assertEquals("INVALID_OTP", ex.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    @DisplayName("verifyOtp - expired OTP")
    void verifyOtp_ExpiredOtp() {
        Otp otp = new Otp();
        otp.setEmail("user@example.com");
        otp.setOtp("123456");
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(otpRepository.findTopByEmailOrderByCreationTimeDesc("user@example.com"))
                .thenReturn(Optional.of(otp));

        JobPortalException ex = assertThrows(JobPortalException.class,
                () -> userService.verifyOtp("user@example.com", "123456"));

        assertEquals("OTP_EXPIRED", ex.getMessage());
        assertEquals(HttpStatus.GONE, ex.getStatus());
    }

    // ---------- changePassword ----------

    @Test
    @DisplayName("changePassword - success")
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("user@example.com", "NikTiwari@1234");
        User user = new User(1L, "John Doe", "user@example.com", "encodedOldPass", AccountType.EMPLOYER);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("NikTiwari@1234", "encodedOldPass")).thenReturn(false);
        when(passwordEncoder.encode("NikTiwari@1234")).thenReturn("encodedNewPass");

        userService.changePassword(request);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }


    @Test
    @DisplayName("changePassword - user not found")
    void changePassword_UserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest("user@example.com", "newPass123");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        JobPortalException ex = assertThrows(JobPortalException.class,
                () -> userService.changePassword(request));

        assertEquals("USER_NOT_FOUND", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("changePassword - new password same as old")
    void changePassword_NewSameAsOld() {
        ChangePasswordRequest request = new ChangePasswordRequest("user@example.com", "newPass123");
        User user = new User(1L, "John Doe", "user@example.com", "encodedOldPass", AccountType.EMPLOYER);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPass123", "encodedOldPass")).thenReturn(true);

        JobPortalException ex = assertThrows(JobPortalException.class,
                () -> userService.changePassword(request));

        assertEquals("NEW_PASS_NOT_SAME_AS_OLD_PASS", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
