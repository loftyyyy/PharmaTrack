package com.rho.ims.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class AuthResponse {
    private String token;
    private UserResponseDTO userResponseDTO;

    public AuthResponse(String token, UserResponseDTO userResponseDTO){
        this.token = token;
        this.userResponseDTO = userResponseDTO;
    }

}
