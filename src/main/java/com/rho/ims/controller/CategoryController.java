package com.rho.ims.controller;


import com.rho.ims.dto.CategoryCreateDTO;
import com.rho.ims.dto.CategoryResponseDTO;
import com.rho.ims.dto.CategoryUpdateDTO;
import com.rho.ims.model.Category;
import com.rho.ims.respository.CategoryRepository;
import com.rho.ims.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository){
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }


    // CRUD
    @GetMapping
    public ResponseEntity<?> getAllCategories(){
        List<CategoryResponseDTO> categories = categoryService.getAll()
                .stream().map(category -> new CategoryResponseDTO(category)).toList();

        return ResponseEntity.ok().body(categories);


    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable long id){
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok().body(new CategoryResponseDTO(category));

    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO){
        Category category = categoryService.saveCategory(categoryCreateDTO);
        return ResponseEntity.ok().body(new CategoryResponseDTO(category));


    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO){

        Category category = categoryService.updateCategory(id, categoryUpdateDTO);


        return ResponseEntity.ok().body(new CategoryResponseDTO(category));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id){
        if(!categoryRepository.existsById(id)){
            return ResponseEntity.badRequest().body("Category doesn't exist");
        }

        categoryRepository.deleteById(id);

        return ResponseEntity.ok().body("Successfully deleted category");

    }



    // CRUD ends HERE



}
