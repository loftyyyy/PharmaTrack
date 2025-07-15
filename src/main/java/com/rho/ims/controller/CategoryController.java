package com.rho.ims.controller;


import com.rho.ims.dto.CategoryDTO;
import com.rho.ims.dto.UpdateCategoryDTO;
import com.rho.ims.model.Category;
import com.rho.ims.respository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository){
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
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
