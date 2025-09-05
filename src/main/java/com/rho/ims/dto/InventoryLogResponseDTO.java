package com.rho.ims.dto;

import com.rho.ims.enums.ChangeType;
import com.rho.ims.model.InventoryLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class InventoryLogResponseDTO {

    private ProductResponseDTO product;
    private ProductBatchResponseDTO productBatch;
    private ChangeType changeType;
    private Integer quantityChanged;
    private String reason;
    private Long saleId;
    private Long purchaseId;
    private String adjustmentReference;
    private LocalDate createdAt;

    public InventoryLogResponseDTO(InventoryLog inventoryLog){
        this.product = new ProductResponseDTO(inventoryLog.getProduct());
        this.productBatch = new ProductBatchResponseDTO(inventoryLog.getProductBatch());
        this.changeType = inventoryLog.getChangeType();
        this.quantityChanged = inventoryLog.getQuantityChanged();
        this.saleId = inventoryLog.getSale().getId();
        this.purchaseId = inventoryLog.getPurchase().getId();
        this.adjustmentReference = inventoryLog.getAdjustmentReference();
        this.createdAt = inventoryLog.getCreatedAt();
    }

}
