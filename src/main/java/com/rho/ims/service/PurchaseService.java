package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.PurchaseCreateDTO;
import com.rho.ims.dto.PurchaseUpdateDTO;
import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.Purchase;
import com.rho.ims.model.Supplier;
import com.rho.ims.model.User;
import com.rho.ims.respository.PurchaseRepository;
import com.rho.ims.respository.SupplierRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, SupplierRepository supplierRepository, UserRepository userRepository){
        this.purchaseRepository = purchaseRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    public Purchase savePurchase(PurchaseCreateDTO purchaseCreateDTO){
        if(purchaseCreateDTO.getPurchaseStatus() == PurchaseStatus.PENDING){

            Optional<Purchase> existing = purchaseRepository.findBySupplierIdAndPurchaseStatus(purchaseCreateDTO.getSupplierId(), PurchaseStatus.PENDING);

            if(existing.isPresent()){
                throw new DuplicateCredentialException("Supplier already has a pending purchase");
            }
        }

        Supplier supplier = supplierRepository.findById(purchaseCreateDTO.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("supplier not found"));
        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setTotalAmount(purchaseCreateDTO.getTotalAmount());
        purchase.setPurchaseDate(purchaseCreateDTO.getPurchaseDate());
        purchase.setPurchaseStatus(purchaseCreateDTO.getPurchaseStatus());

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        purchase.setCreatedBy(user);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getAll(){

        return purchaseRepository.findAll();
    }

    public Purchase getPurchase(Long id){

        return purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("purchase not found"));
    }

    public Purchase updatePurchase(PurchaseUpdateDTO purchaseUpdateDTO, Long id){
        Purchase purchase = purchaseRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("purchase not found"));

        // Prevents changing status back to pending if another pending purchase exists
        if(purchaseUpdateDTO.getPurchaseStatus() == PurchaseStatus.PENDING && !purchase.getPurchaseStatus().equals(PurchaseStatus.PENDING)){
            Optional<Purchase> existing = purchaseRepository.findBySupplierIdAndPurchaseStatus(purchase.getSupplier().getId(), PurchaseStatus.PENDING);
            if(existing.isPresent() && !existing.get().getId().equals(purchase.getId())){
                throw new DuplicateCredentialException("Supplier already has a different pending purchase.");
            }

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
}
