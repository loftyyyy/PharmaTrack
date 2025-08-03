package com.rho.ims.dto;

import com.rho.ims.model.PurchaseItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseItemResponseDTO {

    private Long purchaseItemId;

    private Long purchaseId;

    private Long productBatchId;

    private Integer quantity;

    private BigDecimal unitPrice;

    public PurchaseItemResponseDTO(PurchaseItem purchaseItem){
        this.purchaseItemId = purchaseItem.getId();
        this.purchaseId = purchaseItem.getPurchase().getId();
        this.productBatchId = purchaseItem.getProductBatch().getId();
        this.quantity = purchaseItem.getQuantity();
        this.unitPrice = purchaseItem.getUnitPrice();
    }

}
