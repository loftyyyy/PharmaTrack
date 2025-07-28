package com.rho.ims.dto;

import com.rho.ims.enums.PurchaseStatus;
import com.rho.ims.model.Purchase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
public class PurchaseResponseDTO {
    private Long purchaseId;
    private Long supplierId;
    private BigDecimal totalAmount;
    private LocalDate purchaseDate;
    private PurchaseStatus purchaseStatus;
    private Long createdBy;

    public PurchaseResponseDTO(Purchase purchase){
        this.purchaseId = purchase.getId();
        this.supplierId = purchase.getSupplier().getId();
        this.totalAmount = purchase.getTotalAmount();
        this.purchaseDate = purchase.getPurchaseDate();
        this.purchaseStatus = purchase.getPurchaseStatus();
        this.createdBy = purchase.getCreatedBy().getId();

    }


}
