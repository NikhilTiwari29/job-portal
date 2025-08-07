package com.jobPortal.entity;

import com.jobPortal.dto.UserDto;
import com.jobPortal.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public UserDto toUserDto(){
        return new UserDto(
                this.id,
                this.name,
                this.email,
                this.password,
                this.accountType
        );
    }
}
