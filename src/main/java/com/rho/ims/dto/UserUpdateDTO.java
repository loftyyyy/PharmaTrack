package com.rho.ims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Current password is required")
    @Size(message = "Current password must be at least 8 characters")
    private String currentPassword;

    @Size(message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

}
