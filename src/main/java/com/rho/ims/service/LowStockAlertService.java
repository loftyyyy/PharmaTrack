package com.rho.ims.service;

import com.rho.ims.model.LowStockAlert;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.respository.LowStockAlertRepository;
import com.rho.ims.respository.ProductBatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LowStockAlertService {
    private final LowStockAlertRepository lowStockAlertRepository;
    private final ProductBatchRepository productBatchRepository;

    public LowStockAlertService(LowStockAlertRepository lowStockAlertRepository, ProductBatchRepository productBatchRepository){
        this.lowStockAlertRepository = lowStockAlertRepository;
        this.productBatchRepository = productBatchRepository;
    }

    @Transactional
    public LowStockAlert checkAndCreateLowStockAlerts(){
        List<ProductBatch> productBatch = productBatchRepository.findByQuantityLessThan(10);

        for(ProductBatch pb : productBatch){


        }
        return null;
    }

    
}
