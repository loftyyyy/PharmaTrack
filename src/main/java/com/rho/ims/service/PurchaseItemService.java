package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.purchaseItem.PurchaseItemUpdateDTO;
import com.rho.ims.model.PurchaseItem;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.PurchaseItemRepository;
import com.rho.ims.respository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
 public class PurchaseItemService {
    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductBatchRepository productBatchRepository;
    private final ProductBatchService productBatchService;

    public PurchaseItemService(PurchaseItemRepository purchaseItemRepository, PurchaseRepository purchaseRepository, ProductBatchRepository productBatchRepository, ProductBatchService productBatchService) {
        this.purchaseItemRepository = purchaseItemRepository;
        this.purchaseRepository = purchaseRepository;
        this.productBatchRepository = productBatchRepository;
        this.productBatchService = productBatchService;
    }

    public List<PurchaseItem> getAll(){
        return purchaseItemRepository.findAll();

    }

    public PurchaseItem getPurchaseItem(Long id){
        PurchaseItem purchaseItem = purchaseItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));

        return purchaseItem;

    }

    public PurchaseItem updatePurchaseItem(PurchaseItemUpdateDTO purchaseItemUpdateDTO, Long id){
        PurchaseItem purchaseItem = purchaseItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));

        purchaseItem.setQuantity(purchaseItemUpdateDTO.getQuantity());
        purchaseItem.setUnitPrice(purchaseItemUpdateDTO.getUnitPrice());

        return purchaseItemRepository.save(purchaseItem);

    }

    public void deletePurchaseItem(Long id){
        PurchaseItem purchaseItem = purchaseItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));
        purchaseItemRepository.delete(purchaseItem);

    }
}
