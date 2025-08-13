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
    private Long productId;
    private Long productBatchId;
    private ChangeType changeType;
    private Integer quantityChanged;
    private String reason;
    private Long saleId;
    private Long purchaseId;
    private String adjustmentReference;
    private LocalDate createdAt;

    public InventoryLogResponseDTO(InventoryLog inventoryLog){
        this.productId = inventoryLog.getProduct().getId();
        this.productBatchId = inventoryLog.getProductBatch().getId();
        this.changeType = inventoryLog.getChangeType();
        this.quantityChanged = inventoryLog.getQuantityChanged();
        this.saleId = inventoryLog.getSale().getId();
        this.purchaseId = inventoryLog.getPurchase().getId();
        this.adjustmentReference = inventoryLog.getAdjustmentReference();
        this.createdAt = inventoryLog.getCreatedAt();
    }






}
