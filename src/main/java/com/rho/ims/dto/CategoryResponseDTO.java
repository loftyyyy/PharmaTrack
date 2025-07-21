package com.rho.ims.dto;

import com.rho.ims.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CategoryResponseDTO {
    private Long id;
    private String name;


    public CategoryResponseDTO(Category category){
        this.id = category.getId();
        this.name = category.getName();


    }



}
