package com.rho.ims.dto;

import com.rho.ims.model.SaleItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class SaleItemResponseDTO {

    private Long saleItemId;
    private Long saleId;
    private Long productBatchId;
    private Integer quantity;
    private BigDecimal subTotal;

    public SaleItemResponseDTO(SaleItem saleItem){
        this.saleItemId = saleItem.getId();
        this.saleId = saleItem.getSale().getId();
        this.productBatchId = saleItem.getProductBatch().getId();
        this.quantity = saleItem.getQuantity();
        this.subTotal = saleItem.getSubTotal();
    }

}
