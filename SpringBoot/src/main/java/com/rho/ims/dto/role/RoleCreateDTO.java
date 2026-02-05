package com.rho.ims.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleCreateDTO {

    @NotBlank(message = "Name cannot be blank!")
    private String name;

}
