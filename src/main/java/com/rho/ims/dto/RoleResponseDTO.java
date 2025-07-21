package com.rho.ims.dto;


import com.rho.ims.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RoleResponseDTO {
    private long id;
    private String name;


    public RoleResponseDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }


}
