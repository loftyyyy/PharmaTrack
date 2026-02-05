package com.rho.ims.dto.stockAdjustment;

import com.rho.ims.enums.AdjustmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StockAdjustmentCreateDTO {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Product batch is required")
    private Long productBatchId;

    @NotNull(message = "Quantity changed is required")
    private Integer quantityChanged;

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;

    @NotBlank(message = "Reason is required")
    private String reason;
}
