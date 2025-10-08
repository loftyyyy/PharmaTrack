package com.rho.ims.dto.productBatch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductBatchCheckRequestDTO {

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;
}
