package com.rho.ims.dto;

import com.rho.ims.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class UserResponseDTO {

    @Setter(AccessLevel.NONE)
    private long id;
    private String username;
    private String email;
    private String roleName;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roleName = user.getRole().getName();
    }
}
