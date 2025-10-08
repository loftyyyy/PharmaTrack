package com.rho.ims.dto.sale;

import com.rho.ims.dto.saleItem.SaleItemCreateDTO;
import com.rho.ims.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SaleCreateDTO {

    private Long customerId;

    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @DecimalMin(value = "0.00", inclusive = true, message = "Discount amount must be greater than or equal to 0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private List<SaleItemCreateDTO> items;

}
