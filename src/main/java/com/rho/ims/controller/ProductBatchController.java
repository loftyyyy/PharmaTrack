package com.rho.ims.controller;

import com.rho.ims.service.ProductBatchService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductBatchController {
    private final ProductBatchService productBatchService;

    public ProductBatchController(ProductBatchService productBatchService){
        this.productBatchService = productBatchService;
    }


    // CRUD:





}
