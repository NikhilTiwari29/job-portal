package com.jobportal.repository;

import com.jobportal.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OTPRepository extends JpaRepository<OTP, String> {
	    List<OTP> findByCreationTimeBefore(LocalDateTime expiryTime);
}
