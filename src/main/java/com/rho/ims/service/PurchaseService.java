package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.*;
import com.rho.ims.enums.BatchStatus;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
import com.rho.ims.wrapper.ProductBatchResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductBatchRepository productBatchRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductBatchService productBatchService;
    private final ProductRepository productRepository;
    private final ProductSupplierService productSupplierService;

    public PurchaseService(PurchaseRepository purchaseRepository, SupplierRepository supplierRepository, UserRepository userRepository, ProductBatchRepository productBatchRepository, InventoryLogRepository inventoryLogRepository, ProductBatchService productBatchService, ProductRepository productRepository, ProductSupplierService productSupplierService){
        this.purchaseRepository = purchaseRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.productBatchRepository = productBatchRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.productBatchService = productBatchService;
        this.productRepository = productRepository;
        this.productSupplierService = productSupplierService;
    }

    public Purchase savePurchase(PurchaseCreateDTO purchaseCreateDTO){

        Optional<Purchase> existing = purchaseRepository.findBySupplierIdAndPurchaseStatus(purchaseCreateDTO.getSupplierId(), PurchaseStatus.PENDING);

        if(existing.isPresent()){
            throw new DuplicateCredentialException("Supplier already has a pending purchase");
        }

        Supplier supplier = supplierRepository.findById(purchaseCreateDTO.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("supplier not found"));
        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setTotalAmount(purchaseCreateDTO.getTotalAmount());
        purchase.setPurchaseDate(purchaseCreateDTO.getPurchaseDate());
        purchase.setPurchaseStatus(PurchaseStatus.PENDING);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setCreatedBy(user);

        List<PurchaseItem> purchaseItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        purchaseRepository.save(purchase);


        for(PurchaseItemCreateDTO purchaseItem : purchaseCreateDTO.getPurchaseItems()){


            PurchaseItem item = new PurchaseItem();
            item.setPurchase(purchase);
            item.setUnitPrice(purchaseItem.getUnitPrice());
            item.setQuantity(purchaseItem.getQuantity());

            //ProductBatch Data

            Product product = productRepository.findById(purchaseItem.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product not found"));

            if(!product.getActive()){
                throw new IllegalStateException("Product is inactive");
            }

            System.out.println("Yes, this was the cause");
            item.setProduct(product);
            item.setBatchNumber(purchaseItem.getBatchNumber());
            item.setProductBatch(null);
            item.setQuantity(purchaseItem.getQuantity());
            item.setPurchasePricePerUnit(purchaseItem.getPurchasePricePerUnit());
            item.setExpiryDate(purchaseItem.getExpiryDate());
            item.setManufacturingDate(purchaseItem.getManufacturingDate());
            item.setLocation(purchaseItem.getLocation());
            item.setBatchQuantity(purchaseItem.getBatchQuantity());
            item.setBatchStatus(BatchStatus.AVAILABLE);

            totalAmount = totalAmount.add(purchaseItem.getPurchasePricePerUnit().multiply(BigDecimal.valueOf(purchaseItem.getQuantity())));

            purchaseItems.add(item);

        }

        purchase.setPurchaseItems(purchaseItems);
        purchase.setTotalAmount(totalAmount);

        // TAX 12%

        BigDecimal taxRate = new BigDecimal("0.12");
        BigDecimal taxAmount = totalAmount.multiply(taxRate);
        BigDecimal grandTotal = totalAmount.add(taxAmount);

        purchase.setTaxAmount(taxAmount);
        purchase.setGrandTotal(grandTotal);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getAll(){

        return purchaseRepository.findAll();
    }

    public Purchase getPurchase(Long id){

        return purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("purchase not found"));
    }

    @Transactional
    public Purchase updatePurchase(PurchaseUpdateDTO purchaseUpdateDTO, Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("purchase not found"));

        PurchaseStatus currentStatus = purchase.getPurchaseStatus();
        PurchaseStatus newStatus = purchaseUpdateDTO.getPurchaseStatus();

        if (newStatus == PurchaseStatus.RECEIVED) {
            throw new IllegalStateException("Use confirmPurchase() to mark as received.");
        }

        if (currentStatus == PurchaseStatus.RECEIVED && newStatus == PurchaseStatus.PENDING) {
            throw new IllegalStateException("Cannot change back to PENDING after marked as RECEIVED.");
        }

        if (newStatus == PurchaseStatus.PENDING && currentStatus != PurchaseStatus.PENDING) {
            Optional<Purchase> existing = purchaseRepository.findBySupplierIdAndPurchaseStatus(
                    purchase.getSupplier().getId(), PurchaseStatus.PENDING);
            if (existing.isPresent() && !existing.get().getId().equals(purchase.getId())) {
                throw new DuplicateCredentialException("Supplier already has a different pending purchase.");
            }
        }

        purchase.setPurchaseStatus(newStatus);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setUpdatedBy(user);

        return purchaseRepository.save(purchase);
    }

    public void deletePurchase(Long id){
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));
        purchaseRepository.delete(purchase);
    }


    @Transactional
    public Purchase confirmPurchase(Long id) {
        System.out.println("CONFIRMED PURCHASE");
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));

        if(purchase.getPurchaseStatus() == PurchaseStatus.RECEIVED){
            throw new IllegalStateException("Purchase already confirmed as received");
        }

        if(purchase.getPurchaseStatus() == PurchaseStatus.CANCELLED){
            throw new IllegalStateException("Cannot confirm a cancelled purchase");
        }

        for(PurchaseItem purchaseItem : purchase.getPurchaseItems()){

            ProductBatchCreateDTO productBatch = ProductBatchCreateDTO.builder()
                    .productId(purchaseItem.getProduct().getId())
                    .batchNumber(purchaseItem.getBatchNumber())
                    .quantity(purchaseItem.getBatchQuantity())
                    .purchasePricePerUnit(purchaseItem.getPurchasePricePerUnit())
                    .expiryDate(purchaseItem.getExpiryDate())
                    .manufacturingDate(purchaseItem.getManufacturingDate())
                    .location(purchaseItem.getLocation())
                    .build();


            ProductSupplierCreateDTO productSupplierCreateDTO = ProductSupplierCreateDTO.builder()
                    .productId(purchaseItem.getProduct().getId())
                    .supplierId(purchase.getSupplier().getId())
                    .preferredSupplier(false)
                    .supplierProductCode("SUP-" + purchase.getSupplier().getName().replace("\\s+", "-") + "-" + purchaseItem.getProduct().getSku())
                    .build();


            // Auto generate product supplier after successful purchase
            productSupplierService.saveProductSupplier(productSupplierCreateDTO);

            ProductBatchResult productBatchResult = productBatchService.findOrCreateProductBatch(productBatch);
            purchaseItem.setProductBatch(productBatchResult.getProductBatch());

            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setProduct(productBatchResult.getProductBatch().getProduct());
            inventoryLog.setProductBatch(productBatchResult.getProductBatch());
            inventoryLog.setPurchase(purchase);
            inventoryLog.setQuantityChanged(purchaseItem.getQuantity());

            if(productBatchResult.isCreatedNew()){
                inventoryLog.setChangeType(ChangeType.INITIAL);
                inventoryLog.setReason("Initial stock for new batch via purchase");
                inventoryLog.setAdjustmentReference("PURCHASE-" + purchase.getId());
            }else{
                inventoryLog.setChangeType(ChangeType.IN);
                inventoryLog.setReason("Stock replenishment (existing batch via purchase)");
                inventoryLog.setAdjustmentReference("PURCHASE-" + purchase.getId());
            }

            User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            inventoryLog.setCreatedBy(user);



            inventoryLogRepository.save(inventoryLog);
        }

        purchase.setPurchaseStatus(PurchaseStatus.RECEIVED);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setUpdatedBy(user);

        return purchaseRepository.save(purchase);

    }

    public Purchase cancelPurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));

        if(purchase.getPurchaseStatus() == PurchaseStatus.RECEIVED) {
            throw new IllegalStateException("Cannot cancel a received purchase");

        }

        if(purchase.getPurchaseStatus() == PurchaseStatus.CANCELLED) {
            throw new IllegalStateException("Purchase already cancelled");
        }

        for(PurchaseItem purchaseItem : purchase.getPurchaseItems()){

            ProductBatchCreateDTO productBatch = ProductBatchCreateDTO.builder()
                    .productId(purchaseItem.getProduct().getId())
                    .batchNumber(purchaseItem.getBatchNumber())
                    .quantity(purchaseItem.getBatchQuantity())
                    .purchasePricePerUnit(purchaseItem.getPurchasePricePerUnit())
                    .expiryDate(purchaseItem.getExpiryDate())
                    .manufacturingDate(purchaseItem.getManufacturingDate())
                    .location(purchaseItem.getLocation())
                    .build();

            ProductBatchResult productBatchResult = productBatchService.findOrCreateProductBatch(productBatch);
            purchaseItem.setProductBatch(productBatchResult.getProductBatch());
        }

        purchase.setPurchaseStatus(PurchaseStatus.CANCELLED);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setUpdatedBy(user);

        return purchaseRepository.save(purchase);

    }
}
