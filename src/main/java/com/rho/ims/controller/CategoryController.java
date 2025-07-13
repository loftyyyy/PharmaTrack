package com.rho.ims.controller;


import com.rho.ims.dto.CategoryDTO;
import com.rho.ims.dto.UpdateCategoryDTO;
import com.rho.ims.model.Category;
import com.rho.ims.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;

    }


    // CRUD
    @GetMapping
    public ResponseEntity<?> getAllCategories(){
        List<CategoryDTO> categories = categoryService.getAll()
                .stream().map(category -> new CategoryDTO(category)).toList();

        return ResponseEntity.ok().body(categories);


    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable long id){
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok().body(new CategoryDTO(category));

    }


    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        Category category = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok().body(new CategoryDTO(category));


    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO){

        Category category = categoryService.updateCategory(id, updateCategoryDTO);


        return ResponseEntity.ok().body(category);



    }



    // CRUD ends HERE



}
