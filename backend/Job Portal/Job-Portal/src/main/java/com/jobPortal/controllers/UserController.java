package com.jobPortal.controllers;

import com.jobPortal.dto.*;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto userDto) throws JobPortalException {
        userDto = userService.registerUser(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody @Valid LoginDto loginDto) throws JobPortalException {
        return new ResponseEntity<>(userService.loginUser(loginDto), HttpStatus.OK);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponseDto> sendOtp(@RequestBody @Valid OtpEmailRequest otpEmailRequest) throws Exception {
        userService.sendOtp(otpEmailRequest.getEmail());
        return ResponseEntity.ok(new ResponseDto("OTP sent successfully"));
    }


    @PostMapping("/verify/otp")
    public ResponseEntity<String> verifyOtp(@RequestBody @Valid OtpVerificationRequest request) throws JobPortalException {
        userService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseDto> changePassword(@RequestBody @Valid ChangePasswordRequest request) throws JobPortalException {
        userService.changePassword(request);
        return ResponseEntity.ok(new ResponseDto("Password changed successfully"));
    }
}
