package com.rho.ims.controller;

import com.rho.ims.dto.SaleCreateDTO;
import com.rho.ims.dto.SaleResponseDTO;
import com.rho.ims.dto.SaleVoidDTO;
import com.rho.ims.model.Sale;
import com.rho.ims.respository.SaleRepository;
import com.rho.ims.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleCreateDTO saleCreateDTO) {
        Sale sale = saleService.saveSale(saleCreateDTO);
        return ResponseEntity.ok().body(new SaleResponseDTO(sale));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<SaleResponseDTO> sales = saleService.getAll().stream().map(sale -> new SaleResponseDTO(sale)).toList();
        return ResponseEntity.ok().body(sales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSale(@PathVariable Long id) {
        Sale sale = saleService.getSale(id);
        return ResponseEntity.ok().body(new SaleResponseDTO(sale));

    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmSale(@PathVariable Long id) {
        Sale sale = saleService.confirmSale(id);
        return ResponseEntity.ok().body(new SaleResponseDTO(sale));
    }

    @PostMapping("/{id}/void")
    public ResponseEntity<?> voidSale(@PathVariable Long id, @Valid @RequestBody SaleVoidDTO saleVoidDTO) {
        Sale sale = saleService.voidSale(id, saleVoidDTO);
        return ResponseEntity.ok().body(new SaleResponseDTO(sale));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSale(@PathVariable Long id) {
        Sale sale = saleService.cancelSale(id);
        return ResponseEntity.ok().body(new SaleResponseDTO(sale));
    }



}
