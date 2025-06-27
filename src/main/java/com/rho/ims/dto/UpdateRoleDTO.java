package com.rho.ims.dto;

import com.rho.ims.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateRoleDTO {
    private String name;

    public UpdateRoleDTO(Role role){
        this.name = role.getName();

    }




}
