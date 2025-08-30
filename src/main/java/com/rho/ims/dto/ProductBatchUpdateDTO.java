package com.rho.ims.dto;

import com.rho.ims.enums.BatchStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ProductBatchUpdateDTO {

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;

    @NotNull(message = "Purchase price per unit is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePricePerUnit;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Manufacturing date is required")
    private LocalDate manufacturingDate;

    @Size(max = 50, message = "location must not exceed 50 characters")
    private String location;

    private BatchStatus batchStatus;

}
