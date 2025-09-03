package com.rho.ims.dto;

import com.rho.ims.enums.ChangeType;
import com.rho.ims.model.StockAdjustment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StockAdjustmentResponseDTO {
    private Long stockAdjustmentId;
    private ProductResponseDTO product;
    private Long productBatchId;
    private ChangeType changeType;
    private Integer quantityChanged;
    private String reason;

    public StockAdjustmentResponseDTO(StockAdjustment stockAdjustment){
        this.stockAdjustmentId = stockAdjustment.getId();
        this.product = new ProductResponseDTO(stockAdjustment.getProduct());
        this.productBatchId = stockAdjustment.getProductBatch().getId();
        this.changeType = stockAdjustment.getChangeType();
        this.quantityChanged = stockAdjustment.getQuantityChanged();
        this.reason = stockAdjustment.getReason();
    }

}
