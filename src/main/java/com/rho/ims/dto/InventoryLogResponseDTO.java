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
    private SaleResponseDTO sale;
    private PurchaseResponseDTO purchase;
    private String adjustmentReference;
    private LocalDate createdAt;

    public InventoryLogResponseDTO(InventoryLog inventoryLog){
        this.product = new ProductResponseDTO(inventoryLog.getProduct());
        this.productBatch = new ProductBatchResponseDTO(inventoryLog.getProductBatch());
        this.changeType = inventoryLog.getChangeType();
        this.sale = inventoryLog.getSale() != null ? new SaleResponseDTO(inventoryLog.getSale()) : null;
        this.purchase = inventoryLog.getPurchase() != null ? new PurchaseResponseDTO(inventoryLog.getPurchase()) : null;
        this.quantityChanged = inventoryLog.getQuantityChanged();
        this.adjustmentReference = inventoryLog.getAdjustmentReference();
        this.reason = inventoryLog.getReason();
        this.createdAt = inventoryLog.getCreatedAt();
    }

}
