package com.rho.ims.dto.report;


import com.rho.ims.dto.product.ProductResponseDTO;
import com.rho.ims.dto.productBatch.ProductBatchResponseDTO;
import com.rho.ims.model.ProductBatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class LowStockDTO {
    private ProductResponseDTO product;
    private ProductBatchResponseDTO productBatch;
    private LocalDateTime timeOfAlert;
    private LocalDateTime resolvedAt;

    public LowStockDTO(ProductBatch productBatch){
        this.product = new ProductResponseDTO(productBatch.getProduct());
        this.productBatch = new ProductBatchResponseDTO(productBatch);
        this.timeOfAlert = LocalDateTime.now();
    }

}
