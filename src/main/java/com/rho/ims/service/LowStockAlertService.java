package com.rho.ims.service;

import com.rho.ims.api.exception.ResourceNotFoundException;
import com.rho.ims.dto.stockAlert.LowStockAlertDTO;
import com.rho.ims.enums.Severity;
import com.rho.ims.model.LowStockAlert;
import com.rho.ims.model.ProductBatch;
import com.rho.ims.respository.LowStockAlertRepository;
import com.rho.ims.respository.ProductBatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LowStockAlertService {
    private final LowStockAlertRepository lowStockAlertRepository;
    private final ProductBatchRepository productBatchRepository;

    public LowStockAlertService(LowStockAlertRepository lowStockAlertRepository, ProductBatchRepository productBatchRepository){
        this.lowStockAlertRepository = lowStockAlertRepository;
        this.productBatchRepository = productBatchRepository;
    }

    @Transactional
    public void checkAndCreateLowStockAlerts(){
        List<ProductBatch> productBatch = productBatchRepository.findAll();

        for(ProductBatch pb : productBatch){
            if(pb.getQuantity() <= pb.getProduct().getMinimumStock()){
                Severity severity = assessSeverity(pb);

                Optional<LowStockAlert> existingAlert = lowStockAlertRepository.findByProductBatch(pb);

                if(existingAlert.isEmpty()){
                    LowStockAlert lowStockAlert = LowStockAlert.builder()
                            .productBatch(pb)
                            .severity(severity)
                            .timeOfAlert(LocalDateTime.now())
                            .resolved(false)
                            .build();

                    lowStockAlertRepository.save(lowStockAlert);
                }else{
                    LowStockAlert lowStockAlert = existingAlert.get();

                    if (severity.compareTo(lowStockAlert.getSeverity()) > 0 || Boolean.TRUE.equals(lowStockAlert.getResolved())) {
                        lowStockAlert.setSeverity(severity);
                        lowStockAlert.setResolved(false);
                        lowStockAlert.setTimeOfAlert(LocalDateTime.now());
                        lowStockAlertRepository.save(lowStockAlert);
                    }
                }

            }
        }
    }

    public boolean checkStockAlertForProductBatch(Long productBatchId){
        ProductBatch productBatch = productBatchRepository.findById(productBatchId).orElseThrow(() -> new ResourceNotFoundException("Product batch not found"));
        return lowStockAlertRepository.existsByProductBatch(productBatch);
    }

    public Severity assessSeverity(ProductBatch productBatch){
        int currentStock = productBatch.getQuantity();
        int reOrderLevel = productBatch.getProduct().getMinimumStock();

        if (currentStock <= 0 || currentStock <= reOrderLevel * 0.25) {
            return Severity.CRITICAL;
        } else if (currentStock <= reOrderLevel * 0.5) {
            return Severity.MEDIUM;
        } else if (currentStock <= reOrderLevel) {
            return Severity.LOW;
        } else {
            return null;
        }
    }
}
