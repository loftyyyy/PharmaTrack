package com.rho.ims.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseItemCreateDTO {

    @NotNull(message = "Purchase is required")
    private Long purchaseId;

    @NotNull(message = "Product Batch is required")
    private ProductBatchCreateDTO productBatch;

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;

    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price must be a positive value")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have up to 8 digits before the decimal and 2 after")
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

}
