package com.jobPortal.service;

import com.jobPortal.dto.UserDto;
import com.jobPortal.entity.User;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) throws JobPortalException {
        Optional<User> isPresentByEMail = userRepository.findByEmail(userDto.getEmail());
        if (isPresentByEMail.isPresent()) throw new JobPortalException("USER_FOUND");
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userDto.toUser();
        User savedUser = userRepository.save(user);
        return savedUser.toUserDto();
    }
}
