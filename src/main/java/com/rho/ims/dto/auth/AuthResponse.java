package com.rho.ims.dto.auth;

import com.rho.ims.dto.UserResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn = 900; // 15 minutes in seconds
    private UserResponseDTO user;

    public AuthResponse(String accessToken, String refreshToken, UserResponseDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

}
