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
    private ProductBatchResponseDTO productBatch;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal purchasePricePerUnit;

    public PurchaseItemResponseDTO(PurchaseItem purchaseItem){
        this.purchaseItemId = purchaseItem.getId();
        this.purchaseId = purchaseItem.getPurchase().getId();
        this.productBatch = new ProductBatchResponseDTO(purchaseItem.getProductBatch());
        this.quantity = purchaseItem.getQuantity();
        this.unitPrice = purchaseItem.getUnitPrice();
        this.purchasePricePerUnit = purchaseItem.getPurchasePricePerUnit();
    }

}
