package com.rho.ims.dto.purchase;

import com.rho.ims.dto.purchaseItem.PurchaseItemCreateDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseCreateDTO {

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Total amount must be at least 0.00")
    @Digits(integer = 10, fraction = 2, message = "Total amount must be a valid decimal with up to 10 digits and 2 decimal places")
    private BigDecimal totalAmount;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    private List<PurchaseItemCreateDTO> purchaseItems;

}

