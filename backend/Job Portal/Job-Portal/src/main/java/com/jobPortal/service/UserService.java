package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequest;
import com.jobPortal.dto.LoginDto;
import com.jobPortal.dto.UserDto;
import com.jobPortal.exception.JobPortalException;
import jakarta.validation.Valid;

public interface UserService {
    UserDto registerUser(UserDto userDto) throws JobPortalException;

    UserDto loginUser(@Valid LoginDto loginDto) throws JobPortalException;

    Boolean sendOtp(String email) throws Exception;

    void verifyOtp(String email, String otp) throws JobPortalException;

    void changePassword(ChangePasswordRequest request) throws JobPortalException;

    void deleteExpiredOtps();

}
