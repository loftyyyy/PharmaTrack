package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.InventoryLogCreateDTO;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLogService {
    private final InventoryLogRepository inventoryLogRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ProductBatchRepository productBatchRepository;

    public InventoryLogService(InventoryLogRepository inventoryLogRepository, UserRepository userRepository, PurchaseRepository purchaseRepository, ProductRepository productRepository, SaleRepository saleRepository, ProductBatchRepository productBatchRepository){
        this.inventoryLogRepository = inventoryLogRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.productBatchRepository = productBatchRepository;
    }


    public InventoryLog saveInventoryLog(InventoryLogCreateDTO inventoryLogCreateDTO){
        Purchase purchase = purchaseRepository.findById(inventoryLogCreateDTO.getPurchaseId()).orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        Product product = productRepository.findById(inventoryLogCreateDTO.getProductId()).orElseThrow();
        Sale sale = saleRepository.findById(inventoryLogCreateDTO.getSaleId()).orElseThrow();
        ProductBatch productBatch = productBatchRepository.findById(inventoryLogCreateDTO.getProductBatchId()).orElseThrow();





        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setSale(sale);
        inventoryLog.setProductBatch(productBatch);
        inventoryLog.setProduct(product);
        inventoryLog.setChangeType(inventoryLogCreateDTO.getChangeType());
        inventoryLog.setPurchase(purchase);
        inventoryLog.setAdjustmentReference(inventoryLogCreateDTO.getAdjustmentReference());
        inventoryLog.setQuantityChanged(inventoryLogCreateDTO.getQuantityChanged());

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        inventoryLog.setCreatedBy(user);

        return inventoryLogRepository.save(inventoryLog);
    }

    public List<InventoryLog> getAll(){
        return inventoryLogRepository.findAll();
    }

    public InventoryLog getInventoryLog(Long id){
        InventoryLog inventoryLog = inventoryLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory log not found"));

        return inventoryLog;

    }


}
