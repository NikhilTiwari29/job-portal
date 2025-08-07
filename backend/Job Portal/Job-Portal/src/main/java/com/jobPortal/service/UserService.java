package com.jobPortal.service;

import com.jobPortal.dto.UserDto;
import com.jobPortal.exception.JobPortalException;

public interface UserService {
    UserDto registerUser(UserDto userDto) throws JobPortalException;
}
