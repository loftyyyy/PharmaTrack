package com.rho.ims.controller;

import com.rho.ims.dto.ProductCreateDTO;
import com.rho.ims.dto.ProductResponseDTO;
import com.rho.ims.model.Product;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.service.ProductService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService, ProductRepository productRepository){
        this.productService = productService;
    }


    // CRUD
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(ProductCreateDTO productCreateDTO){
        Product product = productService.saveProduct(productCreateDTO);
        return ResponseEntity.ok().body(new ProductResponseDTO(product));

    }

    @GetMapping
    public ResponseEntity<?> getAllProduct(){
        List<ProductResponseDTO> productList = productService.getAll().stream().map(product -> new ProductResponseDTO(product)).toList();

        return ResponseEntity.ok().body(productList);


    }






    // CRUD ends HERE










}
