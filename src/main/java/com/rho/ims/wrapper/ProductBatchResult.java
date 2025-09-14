package com.rho.ims.wrapper;

import com.rho.ims.model.ProductBatch;

public class ProductBatchResult {
    private final ProductBatch productBatch;
    private final boolean createdNew;

    public ProductBatchResult(ProductBatch productBatch, boolean createdNew){
        this.productBatch = productBatch;
        this.createdNew = createdNew;
    }

    public ProductBatch getProductBatch() {
        return productBatch;
    }

    public boolean isCreatedNew() {
        return createdNew;
    }
}
