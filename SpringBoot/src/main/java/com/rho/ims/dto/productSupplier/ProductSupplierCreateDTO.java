package com.rho.ims.dto.productSupplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProductSupplierCreateDTO {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    private Boolean preferredSupplier = false;

    @Size(max = 100, message = "Supplier product code must not exceed 100 characters")
    @NotBlank(message = "Product code is required")
    private String supplierProductCode;

}
