package com.rho.ims.dto.productBatch;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductBatchCreateDTO {

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotBlank(message = "Batch number is required")
    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;

    @NotNull(message = "Purchase price per unit is required")
    @DecimalMin(value="0.0", inclusive = false)
    private BigDecimal purchasePricePerUnit;

    @NotNull(message = "Selling price per unit is required")
    @DecimalMin(value="0.0", inclusive = false)
    private BigDecimal sellingPricePerUnit;

    @NotNull(message = "Expire date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Manufacturing date is required")
    private LocalDate manufacturingDate;

    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;

}
