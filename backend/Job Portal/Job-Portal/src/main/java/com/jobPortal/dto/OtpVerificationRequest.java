package com.jobPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationRequest {
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.absent}")
    private String email;
    @NotBlank(message = "{user.otp.absent}")
    @Pattern(regexp = "\\d{6}", message = "{user.otp.invalid}")
    private String otp;
}
