package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.PurchaseCreateDTO;
import com.rho.ims.dto.PurchaseItemCreateDTO;
import com.rho.ims.dto.PurchaseUpdateDTO;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
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

    public PurchaseService(PurchaseRepository purchaseRepository, SupplierRepository supplierRepository, UserRepository userRepository, ProductBatchRepository productBatchRepository, InventoryLogRepository inventoryLogRepository, ProductBatchService productBatchService){
        this.purchaseRepository = purchaseRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.productBatchRepository = productBatchRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.productBatchService = productBatchService;
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

        for(PurchaseItemCreateDTO purchaseItem : purchaseCreateDTO.getPurchaseItems()){

//            TODO: This is where we decide to either prompt the user to create a new product batch or just automatically create it

            ProductBatch productBatch = productBatchService.findOrCreateProductBatch(purchaseItem.getProductBatch());
            PurchaseItem item = new PurchaseItem();
            item.setProductBatch(productBatch);
            item.setPurchase(purchase);
            item.setUnitPrice(purchaseItem.getUnitPrice());
            item.setQuantity(purchaseItem.getQuantity());


            totalAmount = totalAmount.add(purchaseItem.getUnitPrice().multiply(BigDecimal.valueOf(purchaseItem.getQuantity())));

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
    public Purchase updatePurchase(PurchaseUpdateDTO purchaseUpdateDTO, Long id){
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));


        // Prevents changing status back to pending if another pending purchase exists
        if(purchaseUpdateDTO.getPurchaseStatus() == PurchaseStatus.PENDING && !purchase.getPurchaseStatus().equals(PurchaseStatus.PENDING)){
            Optional<Purchase> existing = purchaseRepository.findBySupplierIdAndPurchaseStatus(purchase.getSupplier().getId(), PurchaseStatus.PENDING);
            if(existing.isPresent() && !existing.get().getId().equals(purchase.getId())){
                throw new DuplicateCredentialException("Supplier already has a different pending purchase.");
            }

        }

        if(purchaseUpdateDTO.getPurchaseStatus() == PurchaseStatus.RECEIVED){
            throw new IllegalStateException("Use confirmPurchase() to mark as received.");

        }

        purchase.setPurchaseStatus(purchaseUpdateDTO.getPurchaseStatus());

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
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));

        if(purchase.getPurchaseStatus() == PurchaseStatus.RECEIVED){
            throw new IllegalStateException("Purchase already confirmed as received");
        }

        if(purchase.getPurchaseStatus() == PurchaseStatus.CANCELLED){
            throw new IllegalStateException("Cannot confirm a cancelled purchase");
        }

        for(PurchaseItem purchaseItem : purchase.getPurchaseItems()){
            ProductBatch productBatch = productBatchRepository.findById(purchaseItem.getProductBatch().getId()).orElseThrow(() -> new ResourceNotFoundException("Product batch now found"));

            productBatch.setQuantity(productBatch.getQuantity() + purchaseItem.getQuantity());
            productBatchRepository.save(productBatch);

            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setProduct(productBatch.getProduct());
            inventoryLog.setProductBatch(productBatch);
            inventoryLog.setChangeType(ChangeType.IN);
            inventoryLog.setQuantityChanged(purchaseItem.getQuantity());
            inventoryLog.setPurchase(purchase);
            inventoryLog.setReason("Purchase confirmed");
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

        purchase.setPurchaseStatus(PurchaseStatus.CANCELLED);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setUpdatedBy(user);

        return purchaseRepository.save(purchase);

    }
}
