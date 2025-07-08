package com.rho.ims.controller;


import com.rho.ims.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;

    }


    // CRUD
//    public ResponseEntity<?> getAllCategories(){
//
//    }

    // CRUD ends HERE



}
