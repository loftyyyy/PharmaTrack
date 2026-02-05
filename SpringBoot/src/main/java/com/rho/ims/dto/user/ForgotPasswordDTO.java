package com.rho.ims.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ForgotPasswordDTO {

    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
