package com.jobPortal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}

