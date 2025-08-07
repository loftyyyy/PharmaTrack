package com.rho.ims.dto;

import com.rho.ims.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@NoArgsConstructor
@Getter
@Setter
public class SaleUpdateDTO {

    private Long customerId;

    private String reasonForUpdate;

    @PastOrPresent(message = "Sale date cannot be in the future")
    private LocalDate saleDate;

    private PaymentMethod paymentMethod;

    @DecimalMin(value = "0.00", message = "Discount must be zero or more")
    private BigDecimal discountAmount;

}
