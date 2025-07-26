package com.rho.ims.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductSupplierUpdateDTO {

    private Boolean preferredSupplier;

    @Size(max = 100, message = "Supplier product code must not exceed 100 characters")
    private String supplierProductCode;

}
