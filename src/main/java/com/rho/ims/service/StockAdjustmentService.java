package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.StockAdjustmentCreateDTO;
import com.rho.ims.enums.AdjustmentType;
import com.rho.ims.enums.ChangeType;
import com.rho.ims.model.*;
import com.rho.ims.respository.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final UserRepository userRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public StockAdjustmentService(StockAdjustmentRepository stockAdjustmentRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository, UserRepository userRepository, InventoryLogRepository inventoryLogRepository){
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.userRepository = userRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    @Transactional
    public StockAdjustment saveStockAdjustment(StockAdjustmentCreateDTO stockAdjustmentCreateDTO) {
        Product product = productRepository.findById(stockAdjustmentCreateDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductBatch productBatch = productBatchRepository.findById(stockAdjustmentCreateDTO.getProductBatchId()).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));
        int oldQuantity = productBatch.getQuantity();
        int newQuantity;
        int delta;

        if (stockAdjustmentCreateDTO.getAdjustmentType() == AdjustmentType.CORRECTION) {
            newQuantity = stockAdjustmentCreateDTO.getQuantityChanged();
            delta = newQuantity - oldQuantity;
        } else {
            delta = stockAdjustmentCreateDTO.getQuantityChanged();
            newQuantity = oldQuantity + delta;
            if (newQuantity < 0) {
                throw new IllegalStateException("Stock cannot go below zero");
            }
        }

        productBatch.setQuantity(newQuantity);
        productBatchRepository.save(productBatch);

        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setProduct(product);
        stockAdjustment.setProductBatch(productBatch);
        stockAdjustment.setAdjustmentType(delta > 0 ? AdjustmentType.IN : AdjustmentType.OUT);
        stockAdjustment.setReason(stockAdjustmentCreateDTO.getReason());
        stockAdjustment.setQuantityChanged(delta);
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        stockAdjustment.setCreatedBy(user);
        stockAdjustmentRepository.save(stockAdjustment);

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProduct(product);
        inventoryLog.setProductBatch(productBatch);
        inventoryLog.setReason("Manual Stock Adjustment: " + stockAdjustmentCreateDTO.getReason());
        inventoryLog.setPurchase(null);
        inventoryLog.setSale(null);
        inventoryLog.setChangeType(ChangeType.ADJUST);
        inventoryLog.setQuantityChanged(delta);
        inventoryLog.setCreatedBy(user);
        inventoryLog.setAdjustmentReference("MSA-" + LocalDateTime.now());
        inventoryLogRepository.save(inventoryLog);

        return stockAdjustment;
    }

    public List<StockAdjustment> getAll() {
        return stockAdjustmentRepository.findAll();
    }

    public StockAdjustment getStockAdjustment(Long id){
        return stockAdjustmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock adjustment not found"));
    }

}
