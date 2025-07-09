package com.rho.ims.controller;


import com.rho.ims.dto.CategoryDTO;
import com.rho.ims.model.Category;
import com.rho.ims.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;

    }


    // CRUD
    public ResponseEntity<?> getAllCategories(){
        try{
            List<Category> categories = categoryService.getAll();
            List<CategoryDTO> categoryDTO = categories.stream().map(category -> new CategoryDTO(category)).toList();
            return ResponseEntity.ok().body(categoryDTO);



        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // CRUD ends HERE



}
