package com.rho.ims.controller;

import com.rho.ims.dto.ProductBatchCreateDTO;
import com.rho.ims.dto.ProductBatchResponseDTO;
import com.rho.ims.dto.ProductBatchUpdateDTO;
import com.rho.ims.dto.ProductResponseDTO;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.respository.ProductBatchRepository;
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
    // CRUD:
    @PostMapping("/create")
    public ResponseEntity<?> createProductBatch(@Valid @RequestBody ProductBatchCreateDTO productBatchCreateDTO){
        ProductBatch productBatch = productBatchService.saveProductBatch(productBatchCreateDTO);
        return ResponseEntity.ok().body(new ProductBatchResponseDTO(productBatch));
    }

    @GetMapping
    public ResponseEntity<?> getAllProductBatch(){
        List<ProductBatchResponseDTO> productBatches = productBatchService.getAll().stream().map(productBatch -> new ProductBatchResponseDTO(productBatch)).toList();
        return ResponseEntity.ok().body(productBatches);
    }

    @GetMapping("{id}/batches")
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

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
//        productBatchService.deleteProductBatch(id);
//        return ResponseEntity.ok().body("Product batch successfully deleted");
//    }

}
