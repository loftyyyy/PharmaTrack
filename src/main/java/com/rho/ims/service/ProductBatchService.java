package com.rho.ims.service;

import com.rho.ims.api.exception.DuplicateCredentialException;
import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.ProductBatchCheckRequestDTO;
import com.rho.ims.dto.ProductBatchCreateDTO;
import com.rho.ims.dto.ProductBatchResponseDTO;
import com.rho.ims.dto.ProductBatchUpdateDTO;
import com.rho.ims.enums.BatchStatus;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.model.*;
import com.rho.ims.respository.InventoryLogRepository;
import com.rho.ims.respository.ProductBatchRepository;
import com.rho.ims.respository.ProductRepository;
import com.rho.ims.respository.UserRepository;
import com.rho.ims.wrapper.ProductBatchResult;
import jakarta.transaction.Transactional;
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

        if (productBatchRepository.existsByProductIdAndBatchNumber(productBatchCreateDTO.getProductId(), productBatchCreateDTO.getBatchNumber())) {
            throw new DuplicateCredentialException("batch number", productBatchCreateDTO.getBatchNumber());
        }

        if(productBatchRepository.existsByBatchNumber(productBatchCreateDTO.getBatchNumber())) {
            throw new DuplicateCredentialException("batch number", productBatchCreateDTO.getBatchNumber());
        }


        Product product = productRepository.findById(productBatchCreateDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product", productBatchCreateDTO.getProductId().toString()));

        if(!product.getActive()){
            throw new IllegalStateException("Product is inactive");
        }

        ProductBatch productBatch = new ProductBatch();
        productBatch.setProduct(product);
        productBatch.setBatchNumber(productBatchCreateDTO.getBatchNumber());
        productBatch.setQuantity(productBatchCreateDTO.getQuantity());
        productBatch.setPurchasePricePerUnit(productBatchCreateDTO.getPurchasePricePerUnit());
        productBatch.setSellingPricePerUnit(productBatchCreateDTO.getSellingPricePerUnit());
        productBatch.setExpiryDate(productBatchCreateDTO.getExpiryDate());
        productBatch.setManufacturingDate(productBatchCreateDTO.getManufacturingDate());
        productBatch.setLocation(productBatchCreateDTO.getLocation());
        productBatch.setBatchStatus(BatchStatus.AVAILABLE);
        product.setBatchManaged(Boolean.TRUE);
        productRepository.save(product);

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        productBatch.setCreatedBy(user);

        ProductBatch savedBatch = productBatchRepository.save(productBatch);

       return savedBatch;

    }

    @Transactional
    public ProductBatchResult findOrCreateProductBatch(ProductBatchCreateDTO productBatchCreateDTO){
        Optional<ProductBatch> existing = productBatchRepository.findByProductIdAndBatchNumberAndManufacturingDateAndExpiryDate(productBatchCreateDTO.getProductId(), productBatchCreateDTO.getBatchNumber(), productBatchCreateDTO.getManufacturingDate(), productBatchCreateDTO.getExpiryDate());

        Product product = productRepository.findById(productBatchCreateDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product", productBatchCreateDTO.getProductId().toString()));

        if(!product.getActive()){
            throw new IllegalStateException("Product is inactive");
        }

        if(existing.isPresent()){
            ProductBatch productBatch = existing.get();
            return new ProductBatchResult(productBatch, false);
        }else{
            return new ProductBatchResult(saveProductBatch(productBatchCreateDTO),true);
        }

    }

    public List<ProductBatch> getAll(){
        return productBatchRepository.findAll();
    }

    public ProductBatch getProductBatch(Long id){
        return productBatchRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("product batch id", id.toString()));
    }

    public List<ProductBatch> getProductBatchesFromProduct(Long id){
        return productBatchRepository.findByProductId(id);
    }

    public ProductBatch updateProductBatch(ProductBatchUpdateDTO productBatchUpdateDTO, Long id){
        ProductBatch productBatch = productBatchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product batch", id.toString()));

        productBatch.setQuantity(productBatchUpdateDTO.getQuantity());
        productBatch.setPurchasePricePerUnit(productBatchUpdateDTO.getPurchasePricePerUnit());
        productBatch.setSellingPricePerUnit(productBatchUpdateDTO.getSellingPricePerUnit());
        productBatch.setExpiryDate(productBatchUpdateDTO.getExpiryDate());
        productBatch.setManufacturingDate(productBatchUpdateDTO.getManufacturingDate());
        productBatch.setLocation(productBatchUpdateDTO.getLocation());
        productBatch.setBatchStatus(productBatchUpdateDTO.getBatchStatus());
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        productBatch.setUpdatedBy(user);
        return productBatchRepository.save(productBatch);

    }

    public boolean isExistingProductIdAndBatchNumber(ProductBatchCheckRequestDTO productBatchCheckRequestDTO) {
        return productBatchRepository.existsByProductIdAndBatchNumber(productBatchCheckRequestDTO.getProductId(), productBatchCheckRequestDTO.getBatchNumber());
    }

    public List<ProductBatch> getEarliestBatchForAllProducts(){
        return productBatchRepository.findEarliestBatchForEachProduct();
    }

}
