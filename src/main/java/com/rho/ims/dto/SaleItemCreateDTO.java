package com.rho.ims.dto;

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
public class SaleItemCreateDTO {
    @NotNull(message = "Sale is required")
    private Long saleId;

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Product batch is required")
    private Long productBatchId;

    @Min(1)
    private Integer quantity;

    @DecimalMin("0.0")
    private BigDecimal unitPrice;

}
