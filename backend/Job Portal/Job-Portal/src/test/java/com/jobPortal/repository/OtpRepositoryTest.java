package com.jobPortal.repository;

import com.jobPortal.entity.Otp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    private Otp otp1;
    private Otp otp2;

    @BeforeEach
    void setup() {
        otp1 = new Otp();
        otp1.setEmail("user@example.com");
        otp1.setOtp("123456");
        otp1.setCreationTime(LocalDateTime.now().minusMinutes(5));

        otp2 = new Otp();
        otp2.setEmail("user@example.com");
        otp2.setOtp("654321");
        otp2.setCreationTime(LocalDateTime.now()); // latest OTP
    }

    @Test
    @DisplayName("Should save OTP to database")
    void testSaveOtp() {
        Otp savedOtp = otpRepository.save(otp1);

        assertNotNull(savedOtp.getId());
        assertThat(savedOtp.getEmail()).isEqualTo("user@example.com");
        assertThat(savedOtp.getOtp()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should return latest OTP for email")
    void testFindTopByEmailOrderByCreationTimeDesc() {
        otpRepository.save(otp1);
        otpRepository.save(otp2); // newer

        Optional<Otp> result = otpRepository.findTopByEmailOrderByCreationTimeDesc("user@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getOtp()).isEqualTo("654321");
    }

    @Test
    @DisplayName("Should return OTPs created before a specific time")
    void testFindByCreationTimeBefore() {
        otpRepository.save(otp1); // older
        otpRepository.save(otp2); // newer

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1);
        List<Otp> expiredOtps = otpRepository.findByCreationTimeBefore(expiryTime);

        assertThat(expiredOtps).hasSize(1);
        assertThat(expiredOtps.get(0).getOtp()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should return empty if no OTP found for email")
    void testFindTopByEmail_WhenNoOtpExists() {
        Optional<Otp> result = otpRepository.findTopByEmailOrderByCreationTimeDesc("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty if no expired OTPs found")
    void testFindByCreationTimeBefore_WhenNoneExpired() {
        otpRepository.save(otp2); // not expired

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);
        List<Otp> expiredOtps = otpRepository.findByCreationTimeBefore(expiryTime);

        assertThat(expiredOtps).isEmpty();
    }
}
