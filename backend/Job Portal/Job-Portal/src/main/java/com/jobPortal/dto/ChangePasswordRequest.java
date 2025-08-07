package com.jobPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.absent}")
    private String email;

    @NotBlank(message = "{user.old.password.absent}")
    private String oldPassword;

    @NotBlank(message = "New password must not be blank")
    private String newPassword;
}

