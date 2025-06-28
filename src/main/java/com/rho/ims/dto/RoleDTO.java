package com.rho.ims.dto;

import com.rho.ims.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleDTO {
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    public RoleDTO(Role role) {
        this.name = role.getName();
    }





}
