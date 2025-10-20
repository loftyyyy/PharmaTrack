package com.rho.ims.controller;

import com.rho.ims.dto.productBatch.ProductBatchCheckRequestDTO;
import com.rho.ims.dto.productBatch.ProductBatchCheckResponseDTO;
import com.rho.ims.dto.productBatch.ProductBatchResponseDTO;
import com.rho.ims.dto.productBatch.ProductBatchUpdateDTO;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.service.ProductBatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/productBatches")
@RestController
public class ProductBatchController {

    private final ProductBatchService productBatchService;

    public ProductBatchController(ProductBatchService productBatchService){
        this.productBatchService = productBatchService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProductBatch(){
        List<ProductBatchResponseDTO> productBatches = productBatchService.getAll().stream().map(productBatch -> new ProductBatchResponseDTO(productBatch)).toList();
        return ResponseEntity.ok().body(productBatches);
    }

    @GetMapping("/earliest")
    public ResponseEntity<?> getAllEarliestBatchForAllProducts(){
       List<ProductBatchResponseDTO> earliestProductBatches = productBatchService.getEarliestBatchForAllProducts().stream().map(productBatch -> new ProductBatchResponseDTO(productBatch)).toList();
       return ResponseEntity.ok().body(earliestProductBatches);
    }

    @GetMapping("/{id}/batches")
    public ResponseEntity<?> getBatchesFromProduct(@PathVariable Long id){
        List<ProductBatchResponseDTO> productBatches = productBatchService.getProductBatchesFromProduct(id).stream().map(productBatch -> new ProductBatchResponseDTO(productBatch)).toList();
        return ResponseEntity.ok().body(productBatches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductBatch(@PathVariable Long id){
        ProductBatch productBatch = productBatchService.getProductBatch(id);
        return ResponseEntity.ok().body(new ProductBatchResponseDTO(productBatch));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductBatch(@Valid @RequestBody ProductBatchUpdateDTO productBatchUpdateDTO, @PathVariable Long id){
        ProductBatch productBatch = productBatchService.updateProductBatch(productBatchUpdateDTO, id);
        return ResponseEntity.ok().body(new ProductBatchResponseDTO(productBatch));
    }


    @PostMapping("/check")
    public ResponseEntity<?> checkProductBatchExists(@Valid @RequestBody ProductBatchCheckRequestDTO productBatchCheckRequestDTO){
        System.out.println("Called");
        boolean existing = productBatchService.isExistingProductIdAndBatchNumber(productBatchCheckRequestDTO);
        System.out.println(existing);
        return ResponseEntity.ok().body(new ProductBatchCheckResponseDTO(existing));
    }

}
