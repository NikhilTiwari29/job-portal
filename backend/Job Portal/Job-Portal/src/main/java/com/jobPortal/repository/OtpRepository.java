package com.jobPortal.repository;

import com.jobPortal.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findTopByEmailOrderByGeneratedAtDesc(String email);
}
