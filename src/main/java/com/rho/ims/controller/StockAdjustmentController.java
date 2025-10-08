package com.rho.ims.controller;

import com.rho.ims.dto.stockAdjustment.StockAdjustmentCreateDTO;
import com.rho.ims.dto.stockAdjustment.StockAdjustmentResponseDTO;
import com.rho.ims.model.StockAdjustment;
import com.rho.ims.service.StockAdjustmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stockAdjustments")
public class StockAdjustmentController {
    private final StockAdjustmentService stockAdjustmentService;

    public StockAdjustmentController(StockAdjustmentService stockAdjustmentService){
        this.stockAdjustmentService = stockAdjustmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStockAdjustment(@Valid @RequestBody StockAdjustmentCreateDTO stockAdjustmentCreateDTO) {
        StockAdjustment stockAdjustment = stockAdjustmentService.saveStockAdjustment(stockAdjustmentCreateDTO);
        return ResponseEntity.ok().body(new StockAdjustmentResponseDTO(stockAdjustment));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<StockAdjustmentResponseDTO> stockAdjustments = stockAdjustmentService.getAll().stream().map(stockAdjustment -> new StockAdjustmentResponseDTO(stockAdjustment)).toList();
        return ResponseEntity.ok().body(stockAdjustments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStockAdjustment(@PathVariable Long id) {
        StockAdjustment stockAdjustment = stockAdjustmentService.getStockAdjustment(id);
        return ResponseEntity.ok().body(new StockAdjustmentResponseDTO(stockAdjustment));
    }

}
