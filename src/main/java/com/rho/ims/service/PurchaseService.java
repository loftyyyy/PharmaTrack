package com.rho.ims.service;

import com.rho.ims.respository.PurchaseRepository;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository){
        this.purchaseRepository = purchaseRepository;

    }
}
