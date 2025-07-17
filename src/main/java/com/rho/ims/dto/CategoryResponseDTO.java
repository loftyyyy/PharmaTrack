package com.rho.ims.dto;

import com.rho.ims.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CategoryResponseDTO {
    Long id;
    String name;


    public CategoryResponseDTO(Category category){
        this.id = category.getId();
        this.name = category.getName();


    }



}
