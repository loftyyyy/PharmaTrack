package com.rho.ims.controller;

import com.rho.ims.model.Product;
import com.rho.ims.service.ProductService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }


    // CRUD



    @GetMapping
    public ResponseEntity<?> getAllProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){


        }


        try{

        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error creating product:" + e.getMessage());
        }





    }



    // CRUD ends HERE










}
