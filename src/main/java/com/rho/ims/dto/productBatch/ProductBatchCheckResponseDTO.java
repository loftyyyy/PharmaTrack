package com.rho.ims.dto.productBatch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductBatchCheckResponseDTO {
    Boolean exists;

    public ProductBatchCheckResponseDTO(Boolean exists){
        this.exists = exists;
    }
}
