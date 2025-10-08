package com.rho.ims.dto.productSupplier;

import com.rho.ims.dto.SupplierResponseDTO;
import com.rho.ims.dto.product.ProductResponseDTO;
import com.rho.ims.model.ProductSupplier;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductSupplierResponseDTO {

    private Long productSupplierId;
    private ProductResponseDTO product;
    private SupplierResponseDTO supplier;
    private Boolean preferredSupplier;
    private String supplierProductCode;

    public ProductSupplierResponseDTO(ProductSupplier productSupplier){
        this.productSupplierId = productSupplier.getId();
        this.product = new ProductResponseDTO(productSupplier.getProduct());
        this.supplier = new SupplierResponseDTO(productSupplier.getSupplier());
        this.preferredSupplier = productSupplier.getPreferredSupplier();
        this.supplierProductCode = productSupplier.getSupplierProductCode();
    }

}
