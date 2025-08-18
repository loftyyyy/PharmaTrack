package com.rho.ims.dto;

import com.rho.ims.enums.PurchaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseUpdateDTO {

    @NotNull(message = "Purchase status is required")
    private PurchaseStatus purchaseStatus;

}
