package com.rho.ims.dto;

import com.rho.ims.model.ProductSupplier;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductSupplierResponseDTO {

    private Long productSupplierId;
    private Long productId;
    private Long supplierId;
    private Boolean preferredSupplier;
    private String supplierProductCode;

    public ProductSupplierResponseDTO(ProductSupplier productSupplier){
        this.productSupplierId = productSupplier.getId();
        this.productId = productSupplier.getProduct().getId();
        this.supplierId = productSupplier.getSupplier().getId();
        this.preferredSupplier = productSupplier.getPreferredSupplier();
        this.supplierProductCode = productSupplier.getSupplierProductCode();
    }

}
