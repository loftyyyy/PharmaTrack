package com.rho.ims.service;

import com.rho.ims.respository.LowStockAlertRepository;
import org.springframework.stereotype.Service;

@Service
public class LowStockAlertService {
    private final LowStockAlertRepository lowStockAlertRepository;

    public LowStockAlertService(LowStockAlertRepository lowStockAlertRepository){
        this.lowStockAlertRepository = lowStockAlertRepository;
    }
}
