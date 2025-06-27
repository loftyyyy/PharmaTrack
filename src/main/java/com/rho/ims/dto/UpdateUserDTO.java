package com.rho.ims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UpdateUserDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;

}
