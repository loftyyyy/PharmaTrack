package com.rho.ims.dto.stockAlert;


import com.rho.ims.dto.product.ProductResponseDTO;
import com.rho.ims.dto.productBatch.ProductBatchResponseDTO;
import com.rho.ims.model.LowStockAlert;
import com.rho.ims.model.ProductBatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class LowStockAlertDTO {
    private ProductResponseDTO product;
    private ProductBatchResponseDTO productBatch;
    private LocalDateTime timeOfAlert;
    private LocalDateTime resolvedAt;
    private Boolean resolved;

    public LowStockAlertDTO(LowStockAlert lowStockAlert){
        this.product = new ProductResponseDTO(lowStockAlert.getProductBatch().getProduct());
        this.productBatch = new ProductBatchResponseDTO(lowStockAlert.getProductBatch());
        this.timeOfAlert = lowStockAlert.getTimeOfAlert();
        this.resolved = lowStockAlert.getResolved();
    }

}
