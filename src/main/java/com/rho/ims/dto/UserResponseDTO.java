package com.rho.ims.dto;

import com.rho.ims.model.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private String username;
    private String email;
    private String roleName;

    public UserResponseDTO() {

    }
    public UserResponseDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roleName = user.getRole().getName();
    }
}
