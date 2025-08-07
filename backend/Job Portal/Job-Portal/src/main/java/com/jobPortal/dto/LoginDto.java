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

    private Long id;

    @NotBlank(message = "{user.name.absent}")
    @Size(min = 2, max = 50, message = "{user.name.size}")
    private String name;

    @NotBlank(message = "{user.email.absent}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.password.absent}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{user.password.pattern}"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @NotNull(message = "{user.accountType.absent}")
    private AccountType accountType;

    public User toUser() {
        return new User(
                this.id,
                this.name,
                this.email,
                this.password,
                this.accountType
        );
    }
}
