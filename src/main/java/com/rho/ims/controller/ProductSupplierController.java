package com.rho.ims.controller;

import com.rho.ims.dto.*;
import com.rho.ims.model.ProductSupplier;
import com.rho.ims.service.ProductSupplierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/productSuppliers")
@RestController
public class ProductSupplierController {

    private final ProductSupplierService productSupplierService;

    public ProductSupplierController(ProductSupplierService productSupplierService){
        this.productSupplierService = productSupplierService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProductSupplier(@Valid @RequestBody ProductSupplierCreateDTO productSupplierCreateDTO){
        ProductSupplier productSupplier = productSupplierService.saveProductSupplier(productSupplierCreateDTO);
        return ResponseEntity.ok().body( new ProductSupplierResponseDTO(productSupplier));
    }

    @GetMapping
    public ResponseEntity<?> getAllProductSupplier(){
        List<ProductSupplierResponseDTO> productSuppliers = productSupplierService.getAll().stream().map(productSupplier -> new ProductSupplierResponseDTO(productSupplier)).toList();
        return ResponseEntity.ok().body(productSuppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductSupplier(@PathVariable Long id){
        ProductSupplier productSupplier = productSupplierService.getProductSupplier(id);
        return ResponseEntity.ok().body(new ProductSupplierResponseDTO(productSupplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductSupplier(@Valid @RequestBody ProductSupplierUpdateDTO productSupplierUpdateDTO,@PathVariable Long id){
        ProductSupplier productSupplier = productSupplierService.updateProductSupplier(productSupplierUpdateDTO, id);
        return ResponseEntity.ok().body(new ProductSupplierResponseDTO(productSupplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductSupplier(@PathVariable Long id){
        productSupplierService.deleteProductSupplier(id);
        return ResponseEntity.ok().body("Product supplier successfully deleted");
    }
}
