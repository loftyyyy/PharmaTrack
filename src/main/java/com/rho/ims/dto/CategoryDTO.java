package com.rho.ims.dto;

import com.rho.ims.model.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
public class CategoryDTO {

    @Setter(AccessLevel.NONE)
    long id;

   @NotBlank(message = "Name is required")
   String name;

   public CategoryDTO(Category category){
       this.id = category.getId();
       this.name = category.getName();
   }


}
