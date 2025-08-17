package com.rho.ims.controller;

import com.rho.ims.dto.PurchaseCreateDTO;
import com.rho.ims.dto.PurchaseResponseDTO;
import com.rho.ims.dto.PurchaseUpdateDTO;
import com.rho.ims.dto.SupplierResponseDTO;
import com.rho.ims.model.Purchase;
import com.rho.ims.respository.PurchaseRepository;
import com.rho.ims.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService){
        this.purchaseService = purchaseService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPurchase(@Valid @RequestBody PurchaseCreateDTO purchaseCreateDTO){
        Purchase purchase = purchaseService.savePurchase(purchaseCreateDTO);

        return ResponseEntity.ok().body(new PurchaseResponseDTO(purchase));

    }

    @GetMapping
    public ResponseEntity<?> getAllPurchase(){
        List<PurchaseResponseDTO> purchases = purchaseService.getAll().stream().map(purchase -> new PurchaseResponseDTO(purchase)).toList();

        return ResponseEntity.ok().body(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchase(@PathVariable Long id){
        return ResponseEntity.ok().body(new PurchaseResponseDTO(purchaseService.getPurchase(id)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updatePurchase(@Valid @RequestBody PurchaseUpdateDTO purchaseUpdateDTO, @PathVariable Long id){
        Purchase purchase = purchaseService.updatePurchase(purchaseUpdateDTO, id);

        return ResponseEntity.ok().body(new PurchaseResponseDTO(purchase));

    }

    @DeleteMapping("/{id}")
    public void deletePurchase(@PathVariable Long id){
        purchaseService.deletePurchase(id);

    }




}
