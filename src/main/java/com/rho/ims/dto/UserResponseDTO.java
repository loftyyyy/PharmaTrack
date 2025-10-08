package com.rho.ims.dto;

import com.rho.ims.dto.role.RoleResponseDTO;
import com.rho.ims.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserResponseDTO {

    private long id;
    private String username;
    private String email;
    private RoleResponseDTO role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = new RoleResponseDTO(user.getRole());
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
