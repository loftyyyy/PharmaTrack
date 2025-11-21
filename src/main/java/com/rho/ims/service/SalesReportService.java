package com.rho.ims.service;

import com.rho.ims.respository.SaleItemRepository;
import com.rho.ims.respository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
public class SalesReportService {
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    public SalesReportService(SaleRepository saleRepository, SaleItemRepository saleItemRepository){
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
    }
}

