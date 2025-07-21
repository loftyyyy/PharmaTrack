package com.rho.ims.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ProductBatchUpdateDTO {

    @Min(1)
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePricePerUnit;

    private LocalDate expiryDate;

    private LocalDate manufacturingDate;

    @Size(min = 50)
    private String location;


}
