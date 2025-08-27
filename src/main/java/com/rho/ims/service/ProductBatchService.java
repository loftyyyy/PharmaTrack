package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.ProductBatchCreateDTO;
import com.rho.ims.dto.ProductBatchResponseDTO;
import com.rho.ims.dto.ProductBatchUpdateDTO;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.model.InventoryLog;
import com.rho.ims.model.Product;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.model.User;
import com.rho.ims.respository.InventoryLogRepository;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ProductBatchService {
    private final ProductBatchRepository productBatchRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public ProductBatchService(ProductBatchRepository productBatchRepository, ProductRepository productRepository, UserRepository userRepository, InventoryLogRepository inventoryLogRepository){
        this.productBatchRepository = productBatchRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    public ProductBatch saveProductBatch(ProductBatchCreateDTO productBatchCreateDTO){

        if(productBatchRepository.existsByBatchNumber(productBatchCreateDTO.getBatchNumber())){
            throw new DuplicateCredentialException("batch number", productBatchCreateDTO.getBatchNumber());
        }

        Product product = productRepository.findById(productBatchCreateDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product", productBatchCreateDTO.getProductId().toString()));

        ProductBatch productBatch = new ProductBatch();
        productBatch.setProduct(product);
        productBatch.setBatchNumber(productBatchCreateDTO.getBatchNumber());
        productBatch.setQuantity(productBatchCreateDTO.getQuantity());
        productBatch.setPurchasePricePerUnit(productBatchCreateDTO.getPurchasePricePerUnit());
        productBatch.setExpiryDate(productBatchCreateDTO.getExpiryDate());
        productBatch.setManufacturingDate(productBatchCreateDTO.getManufacturingDate());
        productBatch.setLocation(productBatchCreateDTO.getLocation());

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        productBatch.setCreatedBy(user);

        ProductBatch savedBatch = productBatchRepository.save(productBatch);

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setChangeType(ChangeType.INITIAL);
        inventoryLog.setProduct(product);
        inventoryLog.setProductBatch(productBatch);
        inventoryLog.setQuantityChanged(productBatch.getQuantity());
        inventoryLog.setReason("Initial stock for new batch");
        inventoryLog.setAdjustmentReference("INITIAL-STOCK-BATCH-" + productBatch.getId());
        inventoryLog.setCreatedBy(user);
        inventoryLogRepository.save(inventoryLog);

       return savedBatch;

    }

    public List<ProductBatch> getAll(){
        return productBatchRepository.findAll();
    }

    public ProductBatch getProductBatch(Long id){
        return productBatchRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("product batch id", id.toString()));
    }

    public ProductBatch updateProductBatch(ProductBatchUpdateDTO productBatchUpdateDTO, Long id){
        ProductBatch productBatch = productBatchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product batch", id.toString()));

        productBatch.setQuantity(productBatchUpdateDTO.getQuantity());
        productBatch.setPurchasePricePerUnit(productBatchUpdateDTO.getPurchasePricePerUnit());
        productBatch.setExpiryDate(productBatchUpdateDTO.getExpiryDate());
        productBatch.setManufacturingDate(productBatchUpdateDTO.getManufacturingDate());
        productBatch.setLocation(productBatchUpdateDTO.getLocation());

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        productBatch.setUpdatedBy(user);
        return productBatchRepository.save(productBatch);

    }

    public void deleteProductBatch(Long id){
        ProductBatch productBatch = productBatchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product batch", id.toString()));
        productBatchRepository.delete(productBatch);

    }




}
