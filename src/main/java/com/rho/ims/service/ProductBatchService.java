package com.rho.ims.service;

import com.rho.ims.model.ProductBatch;
import com.rho.ims.respository.ProductBatchRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductBatchService {
    private final ProductBatchRepository productBatchRepository;

    public ProductBatchService(ProductBatchRepository productBatchRepository){
        this.productBatchRepository = productBatchRepository;

    }

    public ProductBatch createProductBatch(){

    }


}
