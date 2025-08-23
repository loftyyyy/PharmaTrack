package com.rho.ims.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class AuthResponse {
    private String token;

    public AuthResponse(String token){
        this.token = token;
    }

}
