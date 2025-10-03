package com.rho.ims.dto;


import com.rho.ims.enums.BatchStatus;
import com.rho.ims.model.ProductBatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ProductBatchResponseDTO {

    private Long productBatchId;
    private ProductResponseDTO product;
    private String batchNumber;
    private Integer quantity;
    private BigDecimal purchasePricePerUnit;
    private BigDecimal sellingPricePerUnit;
    private LocalDate expiryDate;
    private BatchStatus status;
    private LocalDate manufacturingDate;
    private String location;

    private LocalDate expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;

    public ProductBatchResponseDTO(ProductBatch productBatch){
        this.productBatchId = productBatch.getId();
        this.product = new ProductResponseDTO(productBatch.getProduct());
        this.batchNumber = productBatch.getBatchNumber();
        this.quantity = productBatch.getQuantity();
        this. purchasePricePerUnit = productBatch.getPurchasePricePerUnit();
        this.sellingPricePerUnit = productBatch.getSellingPricePerUnit();
        this.status = productBatch.getBatchStatus();
        this.expiryDate = productBatch.getExpiryDate();
        this.manufacturingDate = productBatch.getManufacturingDate();
        this.location = productBatch.getLocation();
        this.expirationDate = productBatch.getExpiryDate();
        this.createdAt = productBatch.getCreatedAt();
        this.updatedAt = productBatch.getUpdatedAt();
        this.createdById = productBatch.getCreatedBy().getId();
    }

}
