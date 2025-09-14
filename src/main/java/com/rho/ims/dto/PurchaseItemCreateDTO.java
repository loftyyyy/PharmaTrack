package com.rho.ims.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseItemCreateDTO {

    @NotNull(message = "Purchase is required")
    private Long purchaseId;

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;

    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price must be a positive value")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have up to 8 digits before the decimal and 2 after")
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotBlank(message = "Batch number is required")
    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer batchQuantity;

    @NotNull(message = "Purchase price per unit is required")
    @DecimalMin(value="0.0", inclusive = false)
    private BigDecimal purchasePricePerUnit;

    @NotNull(message = "Expire date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Manufacturing date is required")
    private LocalDate manufacturingDate;

    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;
}
