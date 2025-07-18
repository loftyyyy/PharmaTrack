package com.rho.ims.dto;

import com.rho.ims.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleUpdateDTO {

    @NotBlank(message = "Name cannot be blank!")
    private String name;

    public RoleUpdateDTO(Role role){
        this.name = role.getName();

    }




}
