package com.rho.ims.service;

import com.rho.ims.respository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
public class SaleService {
    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository){
        this.saleRepository = saleRepository;

    }

}
