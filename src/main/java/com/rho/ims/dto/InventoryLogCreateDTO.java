package com.rho.ims.dto;

import com.rho.ims.enums.ChangeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class InventoryLogCreateDTO {
    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Product batch is required")
    private Long productBatchId;

    @NotNull(message = "Change type is required")
    private ChangeType changeType;

    @NotNull(message = "Quantity changed is required")
    private Integer quantityChanged;

    private String reason;


    private Long saleId;

    private Long purchaseId;

    @Size(max = 100, message = "Adjustment reference must not exceed 100 characters")
    private String adjustmentReference;


}
