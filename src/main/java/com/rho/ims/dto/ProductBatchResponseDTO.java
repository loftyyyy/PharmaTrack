package com.rho.ims.dto;


import com.rho.ims.model.ProductBatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ProductBatchResponseDTO {

    private Long productBatchId;
    private Long productId;
    private String batchNumber;
    private Integer quantity;
    private BigDecimal purchasePricePerUnit;
    private LocalDate expiryDate;
    private LocalDateTime manufacturingDate;
    private String location;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductBatchResponseDTO(ProductBatch productBatch){
        this.productBatchId = productBatch.getId();
        this.productId = productBatch.getProductId().getId();
        this.batchNumber = productBatch.getBatchNumber();
        this.quantity = productBatch.getQuantity();
        this. purchasePricePerUnit = productBatch.getPurchasePricePerUnit();
        this.expiryDate = productBatch.getExpiryDate();
        this.manufacturingDate = productBatch.getManufacturingDate();
        this.location = productBatch.getLocation();
        this.createdAt = productBatch.getCreatedAt();
        this.updatedAt = productBatch.getUpdatedAt();




    }









}
