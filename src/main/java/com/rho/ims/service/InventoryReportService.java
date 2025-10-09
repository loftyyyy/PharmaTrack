package com.rho.ims.service;

import com.rho.ims.respository.InventoryLogRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryReportService {

    private final InventoryLogRepository inventoryLogRepository;

    public InventoryReportService(InventoryLogRepository inventoryLogRepository){
        this.inventoryLogRepository = inventoryLogRepository;
    }

    public
}
