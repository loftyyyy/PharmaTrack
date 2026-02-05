package com.rho.ims.dto.sale;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SaleVoidDTO {

    @NotBlank(message = "Void reason is required")
    private String voidReason;

}
