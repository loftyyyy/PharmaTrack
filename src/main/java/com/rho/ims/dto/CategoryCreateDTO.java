package com.rho.ims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class CategoryCreateDTO {
   @NotBlank(message = "Name is required")
   String name;




}
