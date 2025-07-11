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
        try{
            List<CategoryDTO> categories = categoryService.getAll()
                    .stream().map(category -> new CategoryDTO(category)).toList();

            return ResponseEntity.ok().body(categories);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable long id){
        try{
            Category category = categoryService.getCategory(id);
            return ResponseEntity.ok().body(new CategoryDTO(category));

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        }
        try{
            Category category = categoryService.saveCategory(categoryDTO);
            return ResponseEntity.ok().body(new CategoryDTO(category));

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()) );
            return ResponseEntity.badRequest().body(errors);
        }
        try{

            Category category = categoryService.updateCategory(id, updateCategoryDTO);


            return ResponseEntity.ok().body(category);



        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }



    // CRUD ends HERE



}
