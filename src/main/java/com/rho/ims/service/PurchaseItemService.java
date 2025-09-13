package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.PurchaseItemCreateDTO;
import com.rho.ims.dto.PurchaseItemUpdateDTO;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.model.Purchase;
import com.rho.ims.model.PurchaseItem;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.PurchaseItemRepository;
import com.rho.ims.respository.PurchaseRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public PurchaseItem savePurchaseItem(PurchaseItemCreateDTO purchaseItemCreateDTO){
        Purchase purchase = purchaseRepository.findById(purchaseItemCreateDTO.getPurchaseId()).orElseThrow(() -> new ResourceNotFoundException("purchase not found"));
        ProductBatch productBatch = productBatchService.saveProductBatch(purchaseItemCreateDTO.getProductBatch(), purchase);


        Optional<PurchaseItem> existing = purchaseItemRepository.findByPurchaseIdAndProductBatchId(purchaseItemCreateDTO.getPurchaseId(), productBatch.getId());

        if(existing.isPresent()){
            throw new DuplicateCredentialException("Purchase item for this product batch and purchase already exists.");

        }

        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setPurchase(purchase);
        purchaseItem.setProductBatch(productBatch);
        purchaseItem.setQuantity(purchaseItemCreateDTO.getQuantity());
        purchaseItem.setUnitPrice(purchaseItemCreateDTO.getUnitPrice());


        return purchaseItemRepository.save(purchaseItem);
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
