package com.rho.ims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryUpdateDTO {

    @NotBlank(message = "Name is required!")
    private String name;



}
