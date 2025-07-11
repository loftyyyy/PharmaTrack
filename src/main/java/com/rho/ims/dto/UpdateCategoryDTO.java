package com.rho.ims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCategoryDTO {

    @NotBlank(message = "Name is required!")
    private String name;



}
