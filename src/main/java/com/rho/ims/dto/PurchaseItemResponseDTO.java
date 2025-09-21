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
    private String batchNumber;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal purchasePricePerUnit;

    public PurchaseItemResponseDTO(PurchaseItem purchaseItem){
        this.purchaseItemId = purchaseItem.getId();
        this.purchaseId = purchaseItem.getPurchase().getId();
        this.productName = purchaseItem.getProduct().getName();
        this.batchNumber = purchaseItem.getProductBatch().getBatchNumber();
        this.quantity = purchaseItem.getQuantity();
        this.unitPrice = purchaseItem.getUnitPrice();
        this.purchasePricePerUnit = purchaseItem.getPurchasePricePerUnit();
    }

}
