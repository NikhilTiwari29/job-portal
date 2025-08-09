package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequest;
import com.jobPortal.dto.LoginDto;
import com.jobPortal.dto.UserDto;
import com.jobPortal.entity.Otp;
import com.jobPortal.entity.User;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.OtpRepository;
import com.jobPortal.repository.UserRepository;
import com.jobPortal.utility.Data;
import com.jobPortal.utility.Utility;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Override
    public UserDto registerUser(UserDto userDto) throws JobPortalException {
        Optional<User> isPresentByEMail = userRepository.findByEmail(userDto.getEmail());
        if (isPresentByEMail.isPresent()) throw new JobPortalException("USER_FOUND", HttpStatus.NOT_FOUND);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userDto.toUser();
        User savedUser = userRepository.save(user);
        return savedUser.toUserDto();
    }

    @Override
    public UserDto loginUser(LoginDto loginDto) throws JobPortalException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND",HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new JobPortalException("INVALID_CREDENTIALS",HttpStatus.UNAUTHORIZED);
        return user.toUserDto();
    }

    @Override
    public Boolean sendOtp(String email) throws JobPortalException {
        // 1. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        // 2. Generate OTP
        String otpCode = Utility.generateOtp();

        // 3. Create Otp entity and save it
        Otp otp = new Otp();
        otp.setEmail(user.getEmail());
        otp.setOtp(otpCode);
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUser(user);

        otpRepository.save(otp);

        // 4. Prepare and send HTML email using MimeMessage
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject("Your OTP for JobHook Login");

            String htmlContent = Data.htmlContent.formatted(user.getName(), otpCode);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobPortalException("FAILED_TO_SEND_OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifyOtp(String email, String otpValue) throws JobPortalException {
        Otp latestOtp = otpRepository.findTopByEmailOrderByCreationTimeDesc(email)
                .orElseThrow(() -> new JobPortalException("OTP_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!latestOtp.getOtp().equals(otpValue)) {
            throw new JobPortalException("INVALID_OTP", HttpStatus.UNAUTHORIZED);
        }

        if (latestOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new JobPortalException("OTP_EXPIRED", HttpStatus.GONE);
        }

        otpRepository.delete(latestOtp);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) throws JobPortalException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new JobPortalException("NEW_PASS_SHOULD_NOT_BE_SAME_AS_OLD_PASS", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredOtps() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<Otp> expiredOtps = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOtps.isEmpty()){
            otpRepository.deleteAll(expiredOtps);
            System.out.println("Removed " + expiredOtps.size() + " otps");
        }
    }

}
