package com.rho.ims.controller;

import com.rho.ims.dto.purchaseItem.PurchaseItemResponseDTO;
import com.rho.ims.dto.purchaseItem.PurchaseItemUpdateDTO;
import com.rho.ims.model.PurchaseItem;
import com.rho.ims.service.PurchaseItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchaseItems")
public class PurchaseItemController {

    private final PurchaseItemService purchaseItemService;

    public PurchaseItemController( PurchaseItemService purchaseItemService) {
        this.purchaseItemService = purchaseItemService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPurchaseItem() {
        List<PurchaseItemResponseDTO> purchaseItems = purchaseItemService.getAll().stream().map(purchaseItem -> new PurchaseItemResponseDTO(purchaseItem)).toList();

        return ResponseEntity.ok().body(purchaseItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseItem(@PathVariable Long id) {
        PurchaseItem purchaseItem = purchaseItemService.getPurchaseItem(id);

        return ResponseEntity.ok().body(new PurchaseItemResponseDTO(purchaseItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePurchaseItem(@Valid @RequestBody PurchaseItemUpdateDTO purchaseItemUpdateDTO, @PathVariable Long id) {
        PurchaseItem purchaseItem = purchaseItemService.updatePurchaseItem(purchaseItemUpdateDTO, id);

        return ResponseEntity.ok().body(new PurchaseItemResponseDTO(purchaseItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchaseItem(@PathVariable Long id) {
        purchaseItemService.deletePurchaseItem(id);

        return ResponseEntity.ok().body("Purchase item deleted successfully");
    }

}
