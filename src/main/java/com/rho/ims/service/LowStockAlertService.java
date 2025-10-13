package com.rho.ims.service;

import com.rho.ims.respository.LowStockAlertRepository;
import com.rho.ims.respository.ProductBatchRepository;
import org.springframework.stereotype.Service;

@Service
public class LowStockAlertService {
    private final LowStockAlertRepository lowStockAlertRepository;
    private final ProductBatchRepository productBatchRepository;

    public LowStockAlertService(LowStockAlertRepository lowStockAlertRepository, ProductBatchRepository productBatchRepository){
        this.lowStockAlertRepository = lowStockAlertRepository;
        this.productBatchRepository = productBatchRepository;
    }

    
}
